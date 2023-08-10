package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.models.dtos.RoleUserDto
import java.util.UUID

data class Wg(
    @JsonProperty("id") var id: UUID,
    @JsonProperty("name") var name: String,
    @JsonProperty("invitationCode") var invitationCode: String,
    @JsonProperty("features") var features: List<Feature>,
    @JsonProperty("createdDate") var createdDate: String,
    @JsonProperty("userList") var userList: List<RoleUserDto>
)
