package lmu.gruppe3.wgmanager.finance.dto

import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto

data class InvoiceStatusByUserDto(
    var user: ReducedUserDto,
    var invoicePeriod: InvoicePeriodDto,
    var totalSum: Double,
    var averageAmount: Double,
    var contributed: Double,
    var deviation: Double
)