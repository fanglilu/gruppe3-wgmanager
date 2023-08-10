package lmu.gruppe3.wgmanager.finance.repository

import lmu.gruppe3.wgmanager.finance.domain.Debt
import lmu.gruppe3.wgmanager.finance.domain.DebtKey
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DebtRepository : JpaRepository<Debt, DebtKey> {

    fun findDebtsByInvoicePeriodId(periodId: UUID): List<Debt>

    fun findDebtsByInvoicePeriodWgIdAndSettledFalse(wgId: UUID): List<Debt>

    fun findAllByIdIn(ids: List<DebtKey>): List<Debt>
}