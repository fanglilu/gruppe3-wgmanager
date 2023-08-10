package lmu.gruppe3.wgmanager.finance.domain

import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.finance.dto.ExpenseDto
import lmu.gruppe3.wgmanager.finance.dto.ReducedExpenseDto
import lmu.gruppe3.wgmanager.finance.enum.Recurring
import lmu.gruppe3.wgmanager.user.domain.User
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
data class Expense(
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    var payer: User,

    var price: Double,

    @Enumerated(EnumType.STRING)
    var recurring: Recurring?,

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    var image: ByteArray?,

    @Column(name = "period_id")
    var periodId: UUID,

    var aboId: UUID?

) : BaseEntity() {

    @ManyToOne
    @JoinColumn(name = "period_id", insertable = false, updatable = false)
    lateinit var period: InvoicePeriod

    fun toDto(): ExpenseDto {
        return ExpenseDto(
            id = this.id,
            description = this.description,
            payer = this.payer.toReducedDto(),
            price = this.price,
            recurring = this.recurring,
            expenseDate = this.created,
            image = this.image
        )
    }

    fun toReducedDto(): ReducedExpenseDto {
        return ReducedExpenseDto(
            id = this.id,
            description = this.description,
            payer = this.payer.toReducedDto(),
            price = this.price,
            recurring = this.recurring,
            expenseDate = this.created
        )
    }
}