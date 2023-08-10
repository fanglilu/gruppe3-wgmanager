package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class SuccessDto (
    @JsonProperty("success") var success: Boolean
)