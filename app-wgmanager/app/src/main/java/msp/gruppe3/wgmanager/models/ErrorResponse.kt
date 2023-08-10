package msp.gruppe3.wgmanager.models


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorResponse(
    @JsonProperty("message") val message: String,
)
