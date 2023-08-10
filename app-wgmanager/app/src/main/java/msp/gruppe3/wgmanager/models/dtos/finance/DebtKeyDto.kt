package msp.gruppe3.wgmanager.models.dtos.finance

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class DebtKeyDto(
    @JsonProperty("receiverId") var receiverId: UUID,
    @JsonProperty("debtorId") var debtorId: UUID,
    @JsonProperty("invoicePeriodId") var invoicePeriodId: UUID
)
