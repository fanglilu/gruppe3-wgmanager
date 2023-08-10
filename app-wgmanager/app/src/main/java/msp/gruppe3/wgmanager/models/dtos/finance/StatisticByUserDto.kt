package msp.gruppe3.wgmanager.models.dtos.finance

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto

data class StatisticByUserDto(
    @JsonProperty("currentUser") var currentUser: ReducedUserDto,
    @JsonProperty("totalSum") var totalSum: Double,
    @JsonProperty("averageAmount") var averageAmount: Double,
    @JsonProperty("contributed") var contributed: Double,
    @JsonProperty("deviation") var deviation: Double,
    @JsonProperty("expenses") var expenses: List<ReducedExpenseDto>
)
