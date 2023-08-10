package lmu.gruppe3.wgmanager.list.dto

import lmu.gruppe3.wgmanager.user.domain.User
import java.util.*

data class RegisterItemDto(
    var name: String,
    var description: String,
    var listID: UUID,
    var owner: UUID,
    var isBought: Boolean
)