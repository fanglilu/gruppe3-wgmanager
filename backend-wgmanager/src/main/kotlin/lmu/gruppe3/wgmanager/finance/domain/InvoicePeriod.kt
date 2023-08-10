package lmu.gruppe3.wgmanager.finance.domain

import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.finance.dto.InvoicePeriodDto
import lmu.gruppe3.wgmanager.wg_root.domain.Wg
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class InvoicePeriod(
    var startDate: LocalDateTime,
    var endDate: LocalDateTime?,

    @ColumnDefault("false")
    var invoiced: Boolean,

    @Column(name = "wg_id")
    var wgId: UUID,

    ) : BaseEntity() {
    @ManyToOne
    @JoinColumn(name = "wg_id", insertable = false, updatable = false)
    lateinit var wg: Wg
    fun toDto(): InvoicePeriodDto {
        return InvoicePeriodDto(
            id = this.id,
            startDate = this.startDate,
            endDate = this.endDate,
            invoiced = this.invoiced,
        )
    }
}
