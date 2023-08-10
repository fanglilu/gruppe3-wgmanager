package msp.gruppe3.wgmanager.ui.features.finance.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import msp.gruppe3.wgmanager.common.StringBuilderUtil.Companion.buildPeriodString
import msp.gruppe3.wgmanager.databinding.FragmentFinanceStatisticBinding
import msp.gruppe3.wgmanager.models.dtos.finance.InvoiceStatusByUserDto
import msp.gruppe3.wgmanager.ui.features.finance.FinanceViewModel

private const val TAG = "Finance Statistics"

class FinanceStatisticFragment : Fragment() {
    private var _binding: FragmentFinanceStatisticBinding? = null
    private val binding get() = _binding!!

    private lateinit var financeViewModel: FinanceViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        financeViewModel = ViewModelProvider(requireActivity())[FinanceViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceStatisticBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initObservers()
        Log.e(TAG, "Statistics = ${financeViewModel.statistics.value}")
        val statisticsCurrentPeriod = financeViewModel.statistics.value
        if (statisticsCurrentPeriod == null) {
            financeViewModel.getExpensesFromServer(requireActivity())
        } else {
            setStatistics(statisticsCurrentPeriod)
        }
        val statisticsAllTime = financeViewModel.statisticsAllTime.value
        if (statisticsAllTime == null) {
            financeViewModel.getStatisticsAllTimeByUserDto(requireActivity())
        } else {
            setStatisticsAllTime(statisticsAllTime)
        }

        return root
    }

    private fun setStatistics(statistics: InvoiceStatusByUserDto) {
        // Current period
        binding.financeStatisticsPeriod.text = statistics.invoicePeriod?.let { buildPeriodString(it) }
        binding.totalAverageValue.text = statistics.averageAmount.toString()
        binding.totalContributedValue.text = statistics.contributed.toString()
        binding.totalDeviationValue.text = statistics.deviation.toString()
        binding.totalSumValue.text = statistics.totalSum.toString()
    }

    private fun setStatisticsAllTime(statistics: InvoiceStatusByUserDto) {
        // All time
        binding.financeStatisticsPeriodAll.text = statistics.invoicePeriod?.let { buildPeriodString(it) }
        binding.totalAverageValueAll.text = statistics.averageAmount.toString()
        binding.totalContributedValueAll.text = statistics.contributed.toString()
        binding.totalDeviationValueAll.text = statistics.deviation.toString()
        binding.totalSumValueAll.text = statistics.totalSum.toString()
    }

    private fun initObservers() {
        val statusCurrentPeriodObserver = Observer<InvoiceStatusByUserDto?> {
            Log.e(TAG, it.toString())
            if (it != null)  {
                setStatistics(it)
            }
        }
        financeViewModel.statistics.observe(viewLifecycleOwner, statusCurrentPeriodObserver)

        val statusAllTimeObserver = Observer<InvoiceStatusByUserDto?> {
            Log.e(TAG, it.toString())
            if (it != null)  {
                setStatisticsAllTime(it)
            }
        }
        financeViewModel.statisticsAllTime.observe(viewLifecycleOwner, statusAllTimeObserver)
    }
}