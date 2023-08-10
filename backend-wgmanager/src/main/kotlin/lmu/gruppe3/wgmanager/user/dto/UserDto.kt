package lmu.gruppe3.wgmanager.user.dto

import lmu.gruppe3.wgmanager.wg_root.dto.RoleWgDto
import java.util.*

data class UserDto(
    var id: UUID,
    var email: String,
    var name: String,
    var wgList: MutableList<RoleWgDto> = mutableListOf()
)
