package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageDto(
    @JsonProperty("msg") var msg: String
)
