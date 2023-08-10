package lmu.gruppe3.wgmanager.finance.repository

import lmu.gruppe3.wgmanager.finance.domain.Abo
import lmu.gruppe3.wgmanager.finance.enum.Recurring
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AboRepository : JpaRepository<Abo, UUID> {
    fun findAllByRecurringAndWgId(recurring: Recurring, wgId: UUID): List<Abo>

    fun findAllByWgId(wgId: UUID): List<Abo>
}