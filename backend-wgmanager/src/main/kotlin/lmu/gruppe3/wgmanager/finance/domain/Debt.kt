package lmu.gruppe3.wgmanager.finance.domain

import lmu.gruppe3.wgmanager.common.util.ExpenseUtil
import lmu.gruppe3.wgmanager.finance.dto.DebtDto
import lmu.gruppe3.wgmanager.finance.dto.ReducedDebtDto
import lmu.gruppe3.wgmanager.user.domain.User
import org.hibernate.annotations.ColumnDefault
import javax.persistence.*

@Entity
data class Debt(
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("receiverId")
    @JoinColumn(name = "receiver_id")
    var receiver: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("debtorId")
    @JoinColumn(name = "debtor_id")
    var debtor: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("invoicePeriodId")
    @JoinColumn(name = "invoice_period_id")
    var invoicePeriod: InvoicePeriod,

    var debt: Double,

    @ColumnDefault("false")
    var settled: Boolean
) {
    @EmbeddedId
    private var id: DebtKey = DebtKey(this.receiver.id, this.debtor.id, this.invoicePeriod.id)


    fun toDto(): DebtDto {
        return DebtDto(
            id = this.id.toDto(),
            receiver = this.receiver.toReducedDto(),
            debtor = this.debtor.toReducedDto(),
            invoicePeriod = this.invoicePeriod.toDto(),
            debt = this.debt,
            settled = this.settled
        )
    }

    fun toReducedDto(): ReducedDebtDto {
        return ReducedDebtDto(
            receiver = this.receiver.toReducedDto(),
            debtor = this.debtor.toReducedDto(),
            debt = ExpenseUtil.convertValidPrice(this.debt),
            settled = this.settled
        )
    }
}
