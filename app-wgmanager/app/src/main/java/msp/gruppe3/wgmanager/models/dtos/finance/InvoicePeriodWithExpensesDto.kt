package msp.gruppe3.wgmanager.models.dtos.finance

import com.fasterxml.jackson.annotation.JsonProperty

data class InvoicePeriodWithExpensesDto(
    @JsonProperty("invoicePeriod") var invoicePeriod: InvoicePeriodDto,
    @JsonProperty("expenses") var expenses: List<ReducedExpenseDto>
)
