package lmu.gruppe3.wgmanager.list.dto

import lmu.gruppe3.wgmanager.user.domain.User
import java.util.*

data class RegisterListDto(
    var name: String,
    var wgID: UUID,
    var creator: UUID,
    var value: Double,
    var numItems: Int,
    var private: Boolean
)