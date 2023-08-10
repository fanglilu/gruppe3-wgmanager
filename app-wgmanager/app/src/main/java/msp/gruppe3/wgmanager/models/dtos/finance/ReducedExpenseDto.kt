package msp.gruppe3.wgmanager.models.dtos.finance

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.enums.RecurringExpenses
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto
import java.time.LocalDateTime
import java.util.*

data class ReducedExpenseDto(
    @JsonProperty("id") var id: UUID?,
    @JsonProperty("description") var description: String,
    @JsonProperty("price") var price: Double,
    @JsonProperty("payer") var payer: ReducedUserDto?,
    @JsonProperty("recurring") var recurring: RecurringExpenses?,
    @JsonProperty("expenseDate") var expenseDate: LocalDateTime?)
