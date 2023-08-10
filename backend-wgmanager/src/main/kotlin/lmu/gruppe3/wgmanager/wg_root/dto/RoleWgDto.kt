package lmu.gruppe3.wgmanager.wg_root.dto

import lmu.gruppe3.wgmanager.user.enum.UserRole
import java.time.LocalDateTime
import java.util.*

data class RoleWgDto(
    var id: UUID,
    var invitationCode: String,
    var joinedAt: LocalDateTime,
    var name: String,
    var role: UserRole
)
