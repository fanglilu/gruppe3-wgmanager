package msp.gruppe3.wgmanager.services

import android.util.Log
import msp.gruppe3.wgmanager.common.ErrorUtil
import msp.gruppe3.wgmanager.endpoints.apis.FinanceApi
import msp.gruppe3.wgmanager.endpoints.utils.RetrofitClient
import msp.gruppe3.wgmanager.models.dtos.*
import msp.gruppe3.wgmanager.models.dtos.finance.*
import okhttp3.ResponseBody
import java.util.UUID

// Tag for logging
private const val TAG = "Finance Service"

/**
 * Service for API calls via Retrofit 2 and includes error handling
 *
 * @author Marcello Alte
 */
class FinanceService(token: String) {

    private val retrofit = RetrofitClient.getInstance(token)
    private val financeApi = retrofit.create(FinanceApi::class.java)

    suspend fun getAllExpenses(wgId: UUID): List<InvoicePeriodWithExpensesDto>? {
        val response = financeApi.getAllExpenses(wgId)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun getExpenseById(id: UUID): FinanceExpenseDto? {
        val response = financeApi.getExpenseById(id)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun getMyStatusOfAllExpenses(wgId: UUID): StatisticByUserDto? {
        val response = financeApi.getMyStatusOfAllExpenses(wgId)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun getExpense(wgId: UUID): InvoicePeriodWithExpensesDto? {
        val response = financeApi.getExpenses(wgId, null) // TODo add period id

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun addExpense(wgId: UUID, newExpense: FinanceExpenseDto): FinanceExpenseDto? {
        val response = financeApi.addExpense(wgId, newExpense)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun updateExpense(wgId: UUID, updateExpense: FinanceExpenseDto, forFuture: Boolean?): FinanceExpenseDto? {
        val response = financeApi.updateExpense(wgId, updateExpense, forFuture)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun deleteExpense(wgId: UUID, expenseId:UUID, forFuture: Boolean?): SuccessDto? {
        val response = financeApi.deleteExpense(wgId, expenseId, forFuture)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun cashCrash(periodId: UUID): SuccessDto? {
        val response = financeApi.cashCrash(periodId)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun getStatus(periodId: UUID): InvoiceStatusByUserDto? {
        val response = financeApi.getStatus(periodId)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun unsettledDebts(wgId: UUID): List<DebtDto>? {
        val response = financeApi.unsettledDebts(wgId)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun multipleSettledDebts(debts: List<DebtKeyDto>): List<DebtDto>? {
        Log.e(TAG, "multipleSettledDebts: $debts")
        val response = financeApi.multipleSettledDebts(debts)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }
}