package lmu.gruppe3.wgmanager.finance.service

import lmu.gruppe3.wgmanager.finance.domain.InvoicePeriod
import lmu.gruppe3.wgmanager.finance.repository.InvoicePeriodRepository
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.time.LocalDateTime
import java.util.*

@Service
class InvoicePeriodService(private val invoicePeriodRepository: InvoicePeriodRepository) {

    fun getInvoicePeriodById(periodId: UUID): InvoicePeriod {
        return this.invoicePeriodRepository.findById(periodId)
            .orElseThrow { InvalidParameterException("No invoicePeriod found for id = $periodId") }
    }

    fun getAllInvoicePeriodsByWgId(wgId: UUID): List<InvoicePeriod> {
        return this.invoicePeriodRepository.findAllByWgId(wgId)
    }

    fun getCurrentInvoicePeriod(wgId: UUID): InvoicePeriod {
        return this.invoicePeriodRepository.findFirstByEndDateNullAndWgId(wgId) ?: createNewPeriodForWg(wgId)
    }

    fun getAllCurrentInvoicePeriods(): List<InvoicePeriod> {
        return this.invoicePeriodRepository.findAllByEndDateNull()
    }

    fun checkPeriodIsEditable(periodId: UUID, wgId: UUID) {
        val currentPeriod = this.invoicePeriodRepository.findFirstByEndDateNullAndWgId(wgId)

        if (currentPeriod?.id != periodId) {
            throw IllegalAccessException("Past periods can not be edited!")
        }

    }

    private fun createNewPeriodForWg(wgId: UUID): InvoicePeriod {
        val newPeriod = this.buildNewPeriodForWg(wgId)
        return this.invoicePeriodRepository.saveAndFlush(newPeriod)
    }

    private fun buildNewPeriodForWg(wgId: UUID): InvoicePeriod {
        return InvoicePeriod(
            startDate = LocalDateTime.now(),
            invoiced = false,
            endDate = null,
            wgId = wgId
        )
    }

}