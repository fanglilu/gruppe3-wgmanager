package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*


data class ReducedUserDto(
    @JsonProperty("id") var id: UUID,
    @JsonProperty("name") var name: String
)
