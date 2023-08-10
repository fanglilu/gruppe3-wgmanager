package lmu.gruppe3.wgmanager.finance.dto

import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.*

data class InvoicePeriodDto(
    var id: UUID,
    var startDate: LocalDateTime,
    var endDate: LocalDateTime?,
    @ColumnDefault("false")
    var invoiced: Boolean
)
