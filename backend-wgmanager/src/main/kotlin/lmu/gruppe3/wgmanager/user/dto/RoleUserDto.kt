package lmu.gruppe3.wgmanager.user.dto

import lmu.gruppe3.wgmanager.user.enum.UserRole
import java.util.*

data class RoleUserDto(
    var id: UUID,
    var email: String,
    var name: String,
    var role: UserRole
)
