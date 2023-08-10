package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class UserMinimal(
    @JsonProperty("id") var id: UUID,
    @JsonProperty("name") var name: String,
)
