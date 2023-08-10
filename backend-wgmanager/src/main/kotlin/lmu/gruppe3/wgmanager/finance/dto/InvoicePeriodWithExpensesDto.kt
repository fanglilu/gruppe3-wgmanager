package lmu.gruppe3.wgmanager.finance.dto

data class InvoicePeriodWithExpensesDto(
    var invoicePeriod: InvoicePeriodDto,
    var expenses: List<ReducedExpenseDto>
)
