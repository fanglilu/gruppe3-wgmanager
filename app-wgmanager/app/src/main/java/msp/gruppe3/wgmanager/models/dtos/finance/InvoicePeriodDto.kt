package msp.gruppe3.wgmanager.models.dtos.finance

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class InvoicePeriodDto(
    @JsonProperty("id") var id: UUID?,
    @JsonProperty("startDate") var startDate: LocalDateTime,
    @JsonProperty("endDate") var endDate: LocalDateTime?,
    @JsonProperty("invoiced") var invoiced: Boolean?
)
