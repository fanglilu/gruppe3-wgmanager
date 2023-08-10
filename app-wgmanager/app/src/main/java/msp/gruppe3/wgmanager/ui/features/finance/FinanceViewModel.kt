package msp.gruppe3.wgmanager.ui.features.finance

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import msp.gruppe3.wgmanager.MainViewModel
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.models.dtos.finance.*
import msp.gruppe3.wgmanager.services.FinanceService
import retrofit2.HttpException
import java.util.*
import kotlin.collections.List

private const val TAG = "Finance View Model"

class FinanceViewModel : ViewModel() {
    val allExpensesWithPeriod = MutableLiveData<StatisticByUserDto?>()
    val currentExpensesWithPeriod = MutableLiveData<InvoicePeriodWithExpensesDto?>()
    var balance = MutableLiveData<Double>(0.0)
    var userDebts = MutableLiveData<List<DebtDto>?>()

    var statistics = MutableLiveData<InvoiceStatusByUserDto?>()
    var statisticsAllTime = MutableLiveData<InvoiceStatusByUserDto?>()

    private var wgId: UUID? = null

    fun getStatisticsAllTimeByUserDto(activity: FragmentActivity) {
        val wgId = getWgId(activity)
        Log.e(TAG, "getStatisticsAllTimeByUserDto for wg $wgId")

        if (wgId != null) {
            val token = TokenUtil.getTokenByActivity(activity)
            val financeService = FinanceService(token)

            CoroutineScope(Dispatchers.Main).launch {
                val response = financeService.getMyStatusOfAllExpenses(wgId)
                try {
                    Log.e(TAG, "getStatisticsAllTimeByUserDto response $response")
                    if (response != null) {
                        allExpensesWithPeriod.value = response
                        val invoice = InvoicePeriodDto (
                            null,
                            response.expenses.last().expenseDate!!,
                            null,
                            null,
                        )
                        statisticsAllTime.value = InvoiceStatusByUserDto(
                            null,
                            invoice,
                            response.totalSum,
                            response.averageAmount,
                            response.contributed,
                            response.deviation
                        )
                        balance.value = response.deviation
                    } else {
                        allExpensesWithPeriod.value = null
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    fun handleUserDebts(activity: FragmentActivity, debts: List<DebtDto>) {
        val mainViewModel = ViewModelProvider(activity)[MainViewModel::class.java]
        val userId = mainViewModel.userCurrent.value?.id
        if (userId != null) {
            // Split array
            val (myDebts, notMyDebts) = debts.partition { it.debtor.id == userId }
            Log.e(TAG, "me debts: $myDebts")
            Log.e(TAG, "NOT me debts: $notMyDebts")

            val (myReceived, notMyReceived) = debts.partition { it.receiver.id == mainViewModel.userCurrent.value?.id }
            Log.e(TAG, "me received: $myReceived")
            Log.e(TAG, "NOT me received: $notMyReceived")
            this.userDebts.value = myDebts + myReceived
        }
    }

    fun getUserDebts(activity: FragmentActivity) {
        Log.e(TAG, "getUserDebts")
        val wgId = getWgId(activity)

        if (wgId != null) {
            val token = TokenUtil.getTokenByActivity(activity)
            val financeService = FinanceService(token)

            CoroutineScope(Dispatchers.Main).launch {
                val response = financeService.unsettledDebts(wgId)
                try {
                    Log.e(TAG, "getUserDebts response $response")
                    if (response != null) {
                        handleUserDebts(activity, response)
                        userDebts.value = response
                    } else {
                        userDebts.value = null
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }


    fun getExpensesFromServer(activity: FragmentActivity) {
        Log.e(TAG, "getExpensesFromServer")
        val wgId = getWgId(activity)

        if (wgId != null) {
            val token = TokenUtil.getTokenByActivity(activity)
            val financeService = FinanceService(token)

            CoroutineScope(Dispatchers.Main).launch {
                val response = financeService.getExpense(wgId)
                try {
                    Log.e(TAG, "getExpensesFromServer response $response")
                    if (response != null) {
                        currentExpensesWithPeriod.value = response
                        var refreshStatus = false
                        if (statistics.value == null) {
                            refreshStatus = true
                        }
                        response.invoicePeriod.id?.let { getStatus(activity, it, refreshStatus) }
                    } else {
                        currentExpensesWithPeriod.value = null
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    private fun getStatus(activity: FragmentActivity, periodId: UUID, refresh: Boolean = false) {
        if (statistics.value != null || refresh) {
            Log.e(TAG, "getStatus")
            val token = TokenUtil.getTokenByActivity(activity)
            val financeService = FinanceService(token)

            CoroutineScope(Dispatchers.Main).launch {
                val response = financeService.getStatus(periodId)
                try {
                    Log.e(TAG, "getStatus response $response")
                    if (response != null) {
                        statistics.value = response
                        balance.value = response.deviation
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    fun getWgId(activity: FragmentActivity): UUID? {
        if (wgId != null) {
            return wgId as UUID
        }
        val mainViewModel = ViewModelProvider(activity)[MainViewModel::class.java]
        return mainViewModel.wgCurrent.value?.id
    }
}