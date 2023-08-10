package msp.gruppe3.wgmanager.ui.features.finance.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.databinding.FragmentFinanceSettleUpBinding
import msp.gruppe3.wgmanager.models.dtos.finance.DebtDto
import msp.gruppe3.wgmanager.ui.features.finance.FinanceViewModel
import msp.gruppe3.wgmanager.ui.features.finance.recycler_view.MyDebtAdapter
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.models.dtos.finance.DebtKeyDto
import msp.gruppe3.wgmanager.services.FinanceService
import retrofit2.HttpException

private const val TAG = "SettleUp Fragment"

class FinanceSettleUpFragment : Fragment() {
    private var _binding: FragmentFinanceSettleUpBinding? = null
    private val binding get() = _binding!!

    // For the recycler view which contains the feature options
    private lateinit var adapter: MyDebtAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var financeViewModel: FinanceViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        financeViewModel = ViewModelProvider(requireActivity())[FinanceViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceSettleUpBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Period id
        if (financeViewModel.userDebts.value == null) {
            Log.e(TAG, "Got no user debts")
            financeViewModel.getUserDebts(requireActivity())
        }

        val userDeptObserver =
            Observer<List<DebtDto>?> { debts ->
                Log.e(TAG, "userDeptObserver debts: $debts")
                if (debts != null) {
                    buildDebtCard(debts)
                } else {
                    this.findNavController().navigate(R.id.financeFragment)
                }
            }
        financeViewModel.userDebts.observe(viewLifecycleOwner, userDeptObserver)

        financeViewModel.getUserDebts(requireActivity())

        val buttonSubmit = binding.financeSettleUpSubmit
        buttonSubmit.setOnClickListener { submitSettleUp() }

        return root
    }


    private fun buildDebtCard(debts: List<DebtDto>) {
        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val userId = mainViewModel.userCurrent.value?.id
        if (userId != null) {
            // Initialize the recycler view
            val layoutManager = LinearLayoutManager(context)

            adapter = MyDebtAdapter(debts, userId)

            recyclerView = binding.root.findViewById(R.id.finance_settle_up_recycler_view)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }


    private fun submitSettleUp() {
        Log.e(TAG, "submitSettleUp ${adapter.settledUpDebtList}")
        val debtIds = this.adapter.settledUpDebtList.map { it.id } as List<DebtKeyDto>

        if (debtIds.isEmpty()) {
            findNavController().navigate(R.id.financeFragment)
        } else {
            submitRequest(debtIds)
        }
    }

    private fun submitRequest(debtIds: List<DebtKeyDto>) {
        Log.e(TAG, "Submit to server: $debtIds")
            val token = TokenUtil.getTokenByActivity(requireActivity())
            val financeService = FinanceService(token)

            CoroutineScope(Dispatchers.Main).launch {
                val response = financeService.multipleSettledDebts(debtIds)
                try {
                    if (response != null) {
                        financeViewModel.handleUserDebts(requireActivity(), response)
                        findNavController().navigate(R.id.financeFragment)
                    } else {
                        Toast.makeText(
                            context,
                            "Sorry something went wrong.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }

        }
    }
//
//    private fun buildSettleUpDescription(settleUpDto: SettleUpDto): String {
//        val amountToSettleUp = abs(settleUpDto.amountToSettleUp)
//
//        return "${getString(R.string.you_owe)} ${settleUpDto.userName} $amountToSettleUp"
//    }
}