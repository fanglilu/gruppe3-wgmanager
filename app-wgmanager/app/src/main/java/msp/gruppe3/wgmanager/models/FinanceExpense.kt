package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.enums.RecurringExpenses
import java.time.LocalDateTime

data class FinanceExpense(
    @JsonProperty("id") var id: String,
    @JsonProperty("name") var description: String,
    @JsonProperty("payer") var payer: UserMinimal,
    @JsonProperty("price") var price: Double,
    @JsonProperty("recurring") var recurring: RecurringExpenses?,
    @JsonProperty("createdAt") var createdAt: LocalDateTime,
    @JsonProperty("updatedAt") var updatedAt: LocalDateTime?,
    @JsonProperty("image") var image: ByteArray?,
)



