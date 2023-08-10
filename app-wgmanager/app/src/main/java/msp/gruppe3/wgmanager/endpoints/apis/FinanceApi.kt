package msp.gruppe3.wgmanager.endpoints.apis

import msp.gruppe3.wgmanager.models.dtos.*
import msp.gruppe3.wgmanager.models.dtos.finance.*
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

private const val subURLFiance = "finance/expense"
private const val subURLDebt = "finance/debt"


/**
 * For @Fangli Lu:
 * I expect a list of sorted expenses
 * Sorting: First part of the list are the recurring after that the "normal" expenses take place.
 * All expenses are sorted by date descending
 *
 * May helpful:
 * The start date for recurring is the createdAt field
 */
interface FinanceApi {

    @GET("/$subURLFiance/{id}/all-periods")
    suspend fun getAllExpenses(@Path("id") id: UUID): Response<List<InvoicePeriodWithExpensesDto>>

    @GET("/$subURLFiance/{id}")
    suspend fun getExpenseById(@Path("id") id: UUID): Response<FinanceExpenseDto>

    @POST("/$subURLFiance/{id}/create")
    suspend fun addExpense(@Path("id") id: UUID, @Body newExpense: FinanceExpenseDto): Response<FinanceExpenseDto>

    @POST("/$subURLFiance/{id}/update")
    suspend fun updateExpense(@Path("id") id: UUID, @Body updateExpense: FinanceExpenseDto, @Query("forFuture") forFuture: Boolean?): Response<FinanceExpenseDto>

    @DELETE("/$subURLFiance/{wgId}/{expenseId}")
    suspend fun deleteExpense(@Path("wgId") wgId: UUID, @Path("expenseId") expenseId: UUID, @Query("forFuture") forFuture: Boolean?): Response<SuccessDto>

    @GET("/$subURLFiance/{id}/expenses-by-periodId")
    suspend fun getExpenses(@Path("id") id: UUID, @Query("periodId") periodId: UUID?): Response<InvoicePeriodWithExpensesDto>

    @GET("/$subURLDebt/{periodId}/cash-crash")
    suspend fun cashCrash(@Path("periodId") periodId: UUID): Response<SuccessDto>

    @GET("/$subURLFiance/status/{periodId}")
    suspend fun getStatus(@Path("periodId") periodId: UUID): Response<InvoiceStatusByUserDto>

    @GET("/$subURLFiance/{id}/status-all-expenses")
    suspend fun getMyStatusOfAllExpenses(@Path("id") id: UUID): Response<StatisticByUserDto>

    @GET("/$subURLDebt/{id}/unsettled-debts")
    suspend fun unsettledDebts(@Path("id") id: UUID): Response<List<DebtDto>>

    @POST("/$subURLDebt/multiple-settled")
    suspend fun multipleSettledDebts(@Body debts: List<DebtKeyDto>): Response<List<DebtDto>>
}