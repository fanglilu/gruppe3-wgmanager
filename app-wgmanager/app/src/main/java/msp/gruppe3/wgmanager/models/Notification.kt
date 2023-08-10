package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Notification(
    @JsonProperty("title") var title: String,
    @JsonProperty("content") var content: String,
)
