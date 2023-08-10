package lmu.gruppe3.wgmanager.finance.dto

import lmu.gruppe3.wgmanager.finance.enum.Recurring
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import java.time.LocalDateTime
import java.util.*

data class ExpenseDto(
    var id: UUID?,
    var description: String,
    var price: Double,
    var payer: ReducedUserDto?,
    var recurring: Recurring?,
    var expenseDate: LocalDateTime?,
    var image: ByteArray?
)
