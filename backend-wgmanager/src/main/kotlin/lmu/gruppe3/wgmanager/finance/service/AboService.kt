package lmu.gruppe3.wgmanager.finance.service

import lmu.gruppe3.wgmanager.common.util.AuthUtil
import lmu.gruppe3.wgmanager.finance.domain.Abo
import lmu.gruppe3.wgmanager.finance.domain.Expense
import lmu.gruppe3.wgmanager.finance.domain.InvoicePeriod
import lmu.gruppe3.wgmanager.finance.dto.AboDto
import lmu.gruppe3.wgmanager.finance.dto.ExpenseDto
import lmu.gruppe3.wgmanager.finance.enum.Recurring
import lmu.gruppe3.wgmanager.finance.repository.AboRepository
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.util.*

@Service
class AboService(private val aboRepository: AboRepository) {

    fun getAllAboExpenses(): List<AboDto> {
        return this.aboRepository.findAll().map { it.toDto() }
    }

    fun getAboById(id: UUID): Abo {
        return this.aboRepository.findById(id)
            .orElseThrow { InvalidParameterException("Could not find Abo with id = $id") }
    }

    fun getAllAboExpensesByWgId(wgId: UUID): List<AboDto> {
        return this.aboRepository.findAllByWgId(wgId).map { it.toDto() }
    }

    fun updateAboFromExpense(expense: Expense) {
        val abo = this.getAboById(expense.aboId!!)

        abo.recurring = expense.recurring!!
        abo.description = expense.description
        abo.price = expense.price

        this.aboRepository.saveAndFlush(abo)
    }

    fun createAboFromExpense(expense: ExpenseDto, wgId: UUID): UUID {
        val newAbo = Abo(
            description = expense.description,
            recurring = expense.recurring!!,
            payerId = AuthUtil.getCurrentUserId(),
            price = expense.price,
            wgId = wgId
        )

        val savedAbo = this.aboRepository.saveAndFlush(newAbo)
        return savedAbo.id
    }

    fun buildExpensesByRecurringAndWgIdAndPeriodId(recurring: Recurring, period: InvoicePeriod): List<Expense> {
        return this.aboRepository.findAllByRecurringAndWgId(recurring, period.wgId).map { abo ->
            Expense(
                description = abo.description,
                payer = abo.payer,
                price = abo.price,
                recurring = abo.recurring,
                periodId = period.id,
                image = null,
                aboId = abo.id
            )
        }
    }

    fun deleteAboById(aboId: UUID) {
        this.aboRepository.deleteById(aboId)
    }
}