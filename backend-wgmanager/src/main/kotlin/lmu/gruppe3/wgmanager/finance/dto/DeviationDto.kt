package lmu.gruppe3.wgmanager.finance.dto

import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import java.math.BigDecimal

data class DeviationDto(
    var user: ReducedUserDto,
    var deviation: BigDecimal
)
