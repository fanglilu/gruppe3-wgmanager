package msp.gruppe3.wgmanager.models.dtos.finance

import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto

data class ReducedDebtDto(
    var receiver: ReducedUserDto,
    var debtor: ReducedUserDto,
    var debt: Double,
    var settled: Boolean
)
