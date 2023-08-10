package lmu.gruppe3.wgmanager.finance.repository

import lmu.gruppe3.wgmanager.finance.domain.Expense
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ExpenseRepository : JpaRepository<Expense, UUID> {
    fun findExpensesByPeriodId(periodId: UUID): List<Expense>

    fun findAllByPeriodWgId(wgId: UUID): List<Expense>

}