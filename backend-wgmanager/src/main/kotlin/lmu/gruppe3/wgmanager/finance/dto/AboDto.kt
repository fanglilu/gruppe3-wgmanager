package lmu.gruppe3.wgmanager.finance.dto

import lmu.gruppe3.wgmanager.finance.enum.Recurring
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import java.util.*

data class AboDto(
    var id: UUID,
    var description: String,
    var recurring: Recurring,
    var payer: ReducedUserDto,
    var price: Double
)
