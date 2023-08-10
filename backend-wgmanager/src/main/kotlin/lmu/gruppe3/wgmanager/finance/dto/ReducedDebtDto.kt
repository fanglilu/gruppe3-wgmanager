package lmu.gruppe3.wgmanager.finance.dto

import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto

data class ReducedDebtDto(
    var receiver: ReducedUserDto,
    var debtor: ReducedUserDto,
    var debt: Double,
    var settled: Boolean
)
