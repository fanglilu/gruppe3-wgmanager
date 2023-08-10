package msp.gruppe3.wgmanager.models.dtos.finance

import com.fasterxml.jackson.annotation.JsonProperty

data class SettleUpDto(
    @JsonProperty val userName: String,
    @JsonProperty val amountToSettleUp: Double
)
