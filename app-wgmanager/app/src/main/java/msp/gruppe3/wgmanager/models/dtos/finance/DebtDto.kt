package msp.gruppe3.wgmanager.models.dtos.finance

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto

data class DebtDto(
    @JsonProperty("id") var id: DebtKeyDto?,
    @JsonProperty("receiver") var receiver: ReducedUserDto,
    @JsonProperty("debtor") var debtor: ReducedUserDto,
    @JsonProperty("invoicePeriod") var invoicePeriod: InvoicePeriodDto,
    @JsonProperty("debt") var debt: Double,
    @JsonProperty("settled") var settled: Boolean
)
