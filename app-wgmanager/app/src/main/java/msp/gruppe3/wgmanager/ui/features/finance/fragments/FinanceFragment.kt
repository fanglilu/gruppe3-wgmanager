package msp.gruppe3.wgmanager.ui.features.finance.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.StringBuilderUtil.Companion.buildPeriodString
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentFinanceHomeBinding
import msp.gruppe3.wgmanager.models.dtos.finance.*
import msp.gruppe3.wgmanager.services.FinanceService
import msp.gruppe3.wgmanager.ui.features.finance.FinanceViewModel
import msp.gruppe3.wgmanager.ui.features.finance.recycler_view.MyExpenseAdapter
import retrofit2.HttpException
import kotlin.math.abs

private const val TAG = "Finance Fragment"

/**
 * Finance Home shows all information in one view
 * @author Marcello Alte
 */

class FinanceFragment : Fragment() {
    private var _binding: FragmentFinanceHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var financeViewModel: FinanceViewModel

    // For the recycler view which contains the expenses
    private lateinit var adapter: MyExpenseAdapter
    private lateinit var recyclerView: RecyclerView

    private var showAllPeriods = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        financeViewModel = ViewModelProvider(this)[FinanceViewModel::class.java]

        // Initialize finance data
        getDataFromServer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceHomeBinding.inflate(inflater, container, false)
        initButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver(view)
    }

    private fun setBalanceTitle(balance: Double, prefix: String = "") {
        val financeTextBalanceMoney = binding.financeTextBalanceMoney
        var title: String = prefix
        // Set title to owe or owed
        if (balance == 0.0) {
            title += "You are equal"
            financeTextBalanceMoney.setTextColor(Color.GREEN)

        } else if (balance < 0) {
            title += getString(R.string.you_owe)
            financeTextBalanceMoney.setTextColor(Color.RED)

        } else {
            title += getString(R.string.you_are_owed)
            financeTextBalanceMoney.setTextColor(Color.GREEN)
        }
        binding.financeTextBalanceMoneyTitle.text = title
    }

    private fun getDataFromServer() {
        financeViewModel.getExpensesFromServer(requireActivity())
    }

    private fun initObserver(view: View) {
        // Recycler view of expenses
        val layoutManager = LinearLayoutManager(context)
        val expenseObserver = Observer<InvoicePeriodWithExpensesDto?> {
            if (it != null) {
                Log.e(TAG, "expenseObserver $it")

                binding.financePeriodValue.text = buildPeriodString(it.invoicePeriod)

                adapter = MyExpenseAdapter(it.expenses)

                recyclerView = view.findViewById(R.id.finance_recycler_view_expenses)
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
            }
        }
        financeViewModel.currentExpensesWithPeriod.observe(viewLifecycleOwner, expenseObserver)

        val allExpenseObserver = Observer<StatisticByUserDto?> { listOfFinanceInfo ->
            setExpensesToView(listOfFinanceInfo)
        }
        financeViewModel.allExpensesWithPeriod.observe(viewLifecycleOwner, allExpenseObserver)

        // Balance of user
        val balanceObserver = Observer<Double> {
            binding.financeTextBalanceMoney.text = String.format("%.2f", abs(it))
            setBalanceTitle(it)
        }
        financeViewModel.balance.observe(viewLifecycleOwner, balanceObserver)

        val userDeptObserver =
            Observer<List<DebtDto>?> { debts ->
                Log.e(TAG, "userDeptObserver debts: $debts")
                if (debts.isEmpty()) {
                    binding.financeButtonSettleUp.visibility = GONE
                } else {
                    binding.financeButtonSettleUp.visibility = VISIBLE
                }
            }
        financeViewModel.userDebts.observe(viewLifecycleOwner, userDeptObserver)
        financeViewModel.getUserDebts(requireActivity())

    }

    private fun setExpensesToView(listOfFinanceInfo: StatisticByUserDto?) {
        Log.e(TAG, "allExpenseObserver listOfFinanceInfo: $listOfFinanceInfo")
        if (listOfFinanceInfo != null) {

            // last until now
            val allInvoicePeriod = listOfFinanceInfo.expenses.last().expenseDate?.let {
                InvoicePeriodDto(
                    null,
                    startDate = it,
                    null,
                    null
                )
            }
            binding.financePeriodValue.text = allInvoicePeriod?.let { buildPeriodString(it) }

            var allExpenses: List<ReducedExpenseDto> = emptyList()
            listOfFinanceInfo.expenses.forEach {
                allExpenses += it
            }
            Log.e(TAG, "allExpenseObserver it: ${allInvoicePeriod} and $allExpenses")

            adapter.expensesList = allExpenses
            adapter.notifyDataSetChanged()
        }
    }

    private fun initButton() {
        val btnAddExpense = binding.financeButtonAddExpense
        val btnCashCrash = binding.financeButtonCashCrash
        val btnSettleUp = binding.financeButtonSettleUp
        val btnStatistics = binding.financeButtonStatistics
        val showAllPeriods = binding.financeButtonShowAll

        btnAddExpense.setOnClickListener { addExpense() }
        btnCashCrash.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("I want to send an invoice to all users and notify them about their debts.")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    cashCrash()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show() }
        btnSettleUp.setOnClickListener { settleUp() }
        btnStatistics.setOnClickListener { statistics() }
        showAllPeriods.setOnClickListener { switchPeriods() }
    }

    private fun statistics() {
        this.findNavController().navigate(R.id.financeStatisticsFragment)
    }

    private fun addExpense() {
        this.findNavController().navigate(R.id.financeAddExpenseFragment)
    }

    private fun settleUp() {
        this.findNavController().navigate(R.id.financeSettleUpFragment)
    }

    private fun switchPeriods() {
        if (showAllPeriods) {
            showAllPeriods = false
            binding.financeButtonShowAll.text = getString(R.string.show_all_periods)
            getDataFromServer()
        } else {
            showAllPeriods = true
            Log.e(TAG, "showAllPeriods")
            binding.financeButtonShowAll.text = getString(R.string.show_current_period)
            financeViewModel.getStatisticsAllTimeByUserDto(requireActivity())
        }
    }

    /**
     * Sends a reminder to all group members an notify about the cash crash
     * Response is TRUE, if cash crash was triggered.
     * Response is FALSE, if a cash crash is already in charge.
     */
    private fun cashCrash() {
        val periodId = financeViewModel.currentExpensesWithPeriod.value?.invoicePeriod?.id

        if (periodId != null) {
            val token = TokenUtil.getTokenByActivity(requireActivity())
            val financeService = FinanceService(token)

            CoroutineScope(Dispatchers.Main).launch {
                val response = financeService.cashCrash(periodId)
                try {
                    if (response != null) {
                        if (response.success) {
                            setBalanceTitle(financeViewModel.balance.value!!, "CASH CRASH: ")
                            financeViewModel.getExpensesFromServer(requireActivity())
                        } else {
                            Toast.makeText(
                                context,
                                "Cash Crash is already triggered.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        showToast("Sorry something went wrong.")
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        } else {
            showToast("Sorry something went wrong.")
        }
    }

    /**
     * Show toast helper method
     * Length toast shown: LENGTH_SHORT
     */
    private fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, length).show()
    }
}