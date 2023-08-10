package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class UserDto(
    @JsonProperty("id")
    var id: UUID,
    @JsonProperty("email")
    var email: String,
    @JsonProperty("name")
    var name: String,
    @JsonProperty("wgList")
    var wgList: List<RoleWgDto>
)
