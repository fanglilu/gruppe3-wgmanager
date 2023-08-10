package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.enums.UserRole
import java.util.*

data class RoleUserDto(
    @JsonProperty("id") var id: UUID,
    @JsonProperty("email") var email: String,
    @JsonProperty("name") var name: String,
    @JsonProperty("role") var role: UserRole
)