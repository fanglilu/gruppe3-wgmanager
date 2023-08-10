package lmu.gruppe3.wgmanager.finance.domain

import lmu.gruppe3.wgmanager.finance.dto.DebtKeyDto
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class DebtKey(
    @Column(name = "receiver_id")
    val receiverId: UUID,
    @Column(name = "debtor_id")
    val debtorId: UUID,
    @Column(name = "invoice_period_id")
    val invoicePeriodId: UUID
) : Serializable {

    fun toDto(): DebtKeyDto {
        return DebtKeyDto(
            receiverId = this.receiverId,
            debtorId = this.debtorId,
            invoicePeriodId = this.invoicePeriodId
        )
    }
}
