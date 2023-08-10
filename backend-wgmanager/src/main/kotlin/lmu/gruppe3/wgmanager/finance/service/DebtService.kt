package lmu.gruppe3.wgmanager.finance.service

import lmu.gruppe3.wgmanager.common.util.AuthUtil
import lmu.gruppe3.wgmanager.finance.domain.Debt
import lmu.gruppe3.wgmanager.finance.domain.DebtKey
import lmu.gruppe3.wgmanager.finance.domain.InvoicePeriod
import lmu.gruppe3.wgmanager.finance.dto.DebtDto
import lmu.gruppe3.wgmanager.finance.dto.DebtKeyDto
import lmu.gruppe3.wgmanager.finance.dto.ReducedDebtDto
import lmu.gruppe3.wgmanager.finance.repository.DebtRepository
import lmu.gruppe3.wgmanager.finance.repository.InvoicePeriodRepository
import lmu.gruppe3.wgmanager.finance.util.FinanceUtil
import lmu.gruppe3.wgmanager.user.repository.UserRepository
import lmu.gruppe3.wgmanager.wg_root.service.WgRootService
import lmu.gruppe3.wgmanager.wg_root.service.WgValidationService
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.time.LocalDateTime
import java.util.*


@Service
class DebtService(
    private val debtRepository: DebtRepository,
    private val periodRepository: InvoicePeriodRepository,
    private val userRepository: UserRepository,
    private val invoicePeriodRepository: InvoicePeriodRepository,
    private val wgValidationService: WgValidationService,
    private val expenseService: ExpenseService,
    private val wgRootService: WgRootService,
    private val financeNotificationService: FinanceNotificationService
) {

    fun findDebtById(key: DebtKey): Debt {
        return this.debtRepository.findById(key)
            .orElseThrow { InvalidParameterException("No debt found for key: $key !") }
    }

    fun getDebtsByPeriodId(periodId: UUID): List<ReducedDebtDto> {
        val invoicePeriod = this.getPeriodById(periodId)

        this.wgValidationService.checkCurrentUserInWg(invoicePeriod.wgId)
        val availableDebts = this.debtRepository.findDebtsByInvoicePeriodId(periodId).map { it.toReducedDto() }
        return availableDebts.ifEmpty {
            if (invoicePeriod.endDate == null) {
                throw IllegalAccessException("InvoicePeriod (id: $periodId) has no endDate, so no cash crash yet!")
            }
            createDebtsByPeriodId(periodId)
        }
    }

    fun getMyDebtsByPeriodId(periodId: UUID): List<ReducedDebtDto> {
        val allDebts = this.getDebtsByPeriodId(periodId)
        val currentUserId = AuthUtil.getCurrentUserId()
        return allDebts.filter { it.debtor.id == currentUserId || it.receiver.id == currentUserId }
    }

    fun getMyUnsettledDebtsByWgId(wgId: UUID): List<DebtDto> {
        val currentUserId = AuthUtil.getCurrentUserId()
        val result = this.debtRepository.findDebtsByInvoicePeriodWgIdAndSettledFalse(wgId)
            .filter { it.debtor.id == currentUserId || it.receiver.id == currentUserId }
        return result.map { it.toDto() }
    }

    fun createDebt(receiverId: UUID, periodId: UUID): DebtDto {
        val debt = Debt(
            receiver = this.userRepository.getReferenceById(receiverId),
            debtor = this.userRepository.getReferenceById(AuthUtil.getCurrentUserId()),
            invoicePeriod = this.invoicePeriodRepository.getReferenceById(periodId),
            debt = 100.00,
            settled = false
        )

        return this.debtRepository.saveAndFlush(debt).toDto()
    }

    fun settleDebt(periodId: UUID, debtDto: ReducedDebtDto): ReducedDebtDto {
        val key = DebtKey(debtDto.receiver.id, debtDto.debtor.id, periodId)
        this.validateSettleDebtAccess(key)

        val debt = this.findDebtById(key)
        debt.settled = debtDto.settled
        this.checkPeriodIsInvoiced(periodId)

        return this.debtRepository.saveAndFlush(debt).toReducedDto()
    }

    fun settleMultipleDebts(debts: List<DebtKeyDto>): List<DebtDto> {
        val currentUserId = AuthUtil.getCurrentUserId()
        val debtKeys = debts.map { DebtKey(it.receiverId, it.debtorId, it.invoicePeriodId) }
        val accessAllowedDebtKeys = debtKeys.filter { it.receiverId == currentUserId || it.debtorId == currentUserId }

        val settleDebts = this.debtRepository.findAllByIdIn(accessAllowedDebtKeys)
        settleDebts.forEach { it.settled = true }

        this.financeNotificationService.sendSettleUpNotification(settleDebts)
        return this.debtRepository.saveAllAndFlush(settleDebts).map { it.toDto() }
    }

    fun createDebtsByPeriodId(periodId: UUID): List<ReducedDebtDto> {
        val deviations = this.expenseService.calculateDeviationByPeriodId(periodId)
        val sortedDeviations = FinanceUtil.splitDebtorsAndReceivers(deviations)
        val receivers = sortedDeviations[0]
        val debtors = sortedDeviations[1]
        val debtsResult = FinanceUtil.createNewDebts(receivers, debtors)

        val debtsToSave = debtsResult.map {
            Debt(
                receiver = this.userRepository.getReferenceById(it.receiver.id),
                debtor = this.userRepository.getReferenceById(it.debtor.id),
                invoicePeriod = this.periodRepository.getReferenceById(periodId),
                debt = it.debt,
                settled = false
            )
        }

        return this.debtRepository.saveAllAndFlush(debtsToSave).map { it.toReducedDto() }
    }

    fun cashCrashByPeriodId(periodId: UUID) {
        val oldPeriod = this.getPeriodById(periodId)
        oldPeriod.endDate = LocalDateTime.now()
        val periodsToSave = mutableListOf<InvoicePeriod>()

        val newPeriod = this.buildNewPeriodForWg(oldPeriod.wgId)
        periodsToSave.add(oldPeriod)
        periodsToSave.add(newPeriod)
        this.invoicePeriodRepository.saveAllAndFlush(periodsToSave)

        val allFlatMateStatus = this.expenseService.calculateTotalExpensesOfAll(oldPeriod.id)
        this.financeNotificationService.sendCashCrashNotification(allFlatMateStatus)

        this.createDebtsByPeriodId(periodId)
    }

    fun checkPeriodIsInvoiced(periodId: UUID): Boolean {
        val debts = this.debtRepository.findDebtsByInvoicePeriodId(periodId)
        val period = this.getPeriodById(periodId)

        if (debts.isNotEmpty()) {
            val allDebtsSettled = debts.all { it.settled }
            period.invoiced = allDebtsSettled
            return allDebtsSettled
        }

        return period.invoiced
    }

    private fun buildNewPeriodForWg(wgId: UUID): InvoicePeriod {
        return InvoicePeriod(
            startDate = LocalDateTime.now(),
            invoiced = false,
            endDate = null,
            wgId = wgId
        )
    }

    private fun getPeriodById(periodId: UUID): InvoicePeriod {
        return this.periodRepository.findById(periodId)
            .orElseThrow { InvalidParameterException("No invoicePeriod found for id = $periodId") }
    }

    fun endPeriodAndCreateNextPeriod() {
        val wgIds = this.wgRootService.getAllWgIds()
        val periodsToSave = mutableListOf<InvoicePeriod>()

        wgIds.forEach {
            val currentPeriod = this.invoicePeriodRepository.findFirstByEndDateNullAndWgId(it)

            if (currentPeriod != null) {
                currentPeriod.endDate = LocalDateTime.now()
                periodsToSave.add(currentPeriod)

                this.createDebtsByPeriodId(currentPeriod.id)

                val allFlatMateStatus = this.expenseService.calculateTotalExpensesOfAll(currentPeriod.id)
                this.financeNotificationService.sendCashCrashNotification(allFlatMateStatus)
            }

            val newPeriod = this.buildNewPeriodForWg(it)
            periodsToSave.add(newPeriod)
        }

        this.invoicePeriodRepository.saveAllAndFlush(periodsToSave)
    }

    private fun validateSettleDebtAccess(debtKey: DebtKey): DebtKey {
        val currentId = AuthUtil.getCurrentUserId()
        if (debtKey.debtorId != currentId && debtKey.receiverId != currentId) {
            throw IllegalAccessException("Current user (id: $currentId) is not allowed to access debt $debtKey !")
        }
        return debtKey
    }

}