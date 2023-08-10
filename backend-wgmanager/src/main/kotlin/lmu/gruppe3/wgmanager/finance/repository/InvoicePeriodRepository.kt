package lmu.gruppe3.wgmanager.finance.repository

import lmu.gruppe3.wgmanager.finance.domain.InvoicePeriod
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface InvoicePeriodRepository : JpaRepository<InvoicePeriod, UUID> {
    fun findFirstByEndDateNullAndWgId(wgId: UUID): InvoicePeriod?

    fun findAllByEndDateNull(): List<InvoicePeriod>

    fun findAllByWgId(wgId: UUID): List<InvoicePeriod>

}