package lmu.gruppe3.wgmanager.finance.dto

import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto

data class DebtDto(
    var id: DebtKeyDto?,
    var receiver: ReducedUserDto,
    var debtor: ReducedUserDto,
    var invoicePeriod: InvoicePeriodDto,
    var debt: Double,
    var settled: Boolean
)
