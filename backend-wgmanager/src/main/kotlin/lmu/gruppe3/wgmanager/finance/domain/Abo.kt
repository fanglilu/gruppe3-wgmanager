package lmu.gruppe3.wgmanager.finance.domain

import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.finance.dto.AboDto
import lmu.gruppe3.wgmanager.finance.enum.Recurring
import lmu.gruppe3.wgmanager.user.domain.User
import lmu.gruppe3.wgmanager.wg_root.domain.Wg
import java.util.*
import javax.persistence.*

@Entity
data class Abo(
    var description: String,

    @Enumerated(EnumType.STRING)
    var recurring: Recurring,

    @Column(name = "payer_id")
    var payerId: UUID,

    var price: Double,

    @Column(name = "wg_id")
    var wgId: UUID

) : BaseEntity() {

    @ManyToOne
    @JoinColumn(name = "wg_id", insertable = false, updatable = false)
    lateinit var wg: Wg

    @ManyToOne
    @JoinColumn(name = "payer_id", insertable = false, updatable = false)
    lateinit var payer: User
    fun toDto(): AboDto {
        return AboDto(
            id = this.id,
            description = this.description,
            recurring = this.recurring,
            payer = this.payer.toReducedDto(),
            price = this.price,
        )
    }
}
