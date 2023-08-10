package lmu.gruppe3.wgmanager.wg_root.dto

import FeatureDto
import lmu.gruppe3.wgmanager.user.dto.RoleUserDto
import java.time.LocalDateTime
import java.util.*

data class WgDto(
    var id: UUID,
    var invitationCode: String,
    var name: String,
    var features: List<FeatureDto>,
    var createdDate: LocalDateTime,
    val userList: MutableList<RoleUserDto> = mutableListOf()
)