package lmu.gruppe3.wgmanager.finance.dto

import java.util.*

data class DebtKeyDto(
    var receiverId: UUID,
    var debtorId: UUID,
    var invoicePeriodId: UUID
)
