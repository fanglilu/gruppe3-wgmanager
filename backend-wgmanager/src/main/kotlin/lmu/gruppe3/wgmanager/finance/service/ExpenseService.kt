package lmu.gruppe3.wgmanager.finance.service

import lmu.gruppe3.wgmanager.common.util.AuthUtil
import lmu.gruppe3.wgmanager.common.util.ExpenseUtil
import lmu.gruppe3.wgmanager.finance.domain.Expense
import lmu.gruppe3.wgmanager.finance.domain.InvoicePeriod
import lmu.gruppe3.wgmanager.finance.dto.*
import lmu.gruppe3.wgmanager.finance.enum.Recurring
import lmu.gruppe3.wgmanager.finance.repository.ExpenseRepository
import lmu.gruppe3.wgmanager.finance.util.FinanceUtil
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import lmu.gruppe3.wgmanager.user.repository.UserRepository
import lmu.gruppe3.wgmanager.user.service.UserService
import lmu.gruppe3.wgmanager.wg_user.service.WgUserService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.rmi.NoSuchObjectException
import java.security.InvalidParameterException
import java.util.*

@Service
class ExpenseService(
    private val expenseRepository: ExpenseRepository,
    private val invoicePeriodService: InvoicePeriodService,
    private val userRepository: UserRepository,
    private val wgUserService: WgUserService,
    private val userService: UserService,
    private val aboService: AboService,
    private val notificationService: FinanceNotificationService
) {

    fun findById(id: UUID): Expense {
        return this.expenseRepository.findById(id)
            .orElseThrow { NoSuchObjectException("Could not find existing expense with id $id") }
    }

    fun findAllByPeriodId(periodId: UUID): List<ReducedExpenseDto> {
        return this.expenseRepository.findExpensesByPeriodId(periodId).map { it.toReducedDto() }
    }

    fun findExpensesOfAllPeriods(wgId: UUID): List<InvoicePeriodWithExpensesDto> {
        val periods = this.invoicePeriodService.getAllInvoicePeriodsByWgId(wgId)
        return periods.map { buildInvoicePeriodWithExpenses(it) }
    }

    fun findExpensesByPeriodId(wgId: UUID, periodId: UUID? = null): InvoicePeriodWithExpensesDto {
        val period = when (periodId) {
            null -> this.invoicePeriodService.getCurrentInvoicePeriod(wgId)
            else -> this.invoicePeriodService.getInvoicePeriodById(periodId)
        }

        return this.buildInvoicePeriodWithExpenses(period)
    }

    fun findAllExpensesOfCurrentUserByWgId(wgId: UUID): StatisticByUserDto {
        val currentUser = this.userService.getCurrentUser()
        val allExpenses = this.expenseRepository.findAllByPeriodWgId(wgId)
        val flatmates = this.wgUserService.getFlatMatesByWgId(wgId)
        val total: BigDecimal = allExpenses.sumOf { BigDecimal.valueOf(it.price) }
        val average: BigDecimal = BigDecimal.valueOf(this.calculateAverage(flatmates, allExpenses))
        val contributed: BigDecimal = this.calculateContributionByUserId(allExpenses, currentUser.id)
        val deviation: BigDecimal = contributed - average

        return StatisticByUserDto(
            currentUser = currentUser.toReducedDto(),
            totalSum = ExpenseUtil.convertValidPrice(total.toDouble()),
            averageAmount = ExpenseUtil.convertValidPrice(average.toDouble()),
            contributed = ExpenseUtil.convertValidPrice(contributed.toDouble()),
            deviation = ExpenseUtil.convertValidPrice(deviation.toDouble()),
            expenses = allExpenses.map { it.toReducedDto() }.sortedBy { it.expenseDate }.reversed()
        )
    }

    private fun buildInvoicePeriodWithExpenses(period: InvoicePeriod): InvoicePeriodWithExpensesDto {
        val expenses = this.findAllByPeriodId(period.id).sortedBy { it.expenseDate }

        return InvoicePeriodWithExpensesDto(
            invoicePeriod = period.toDto(),
            expenses = splitAndSortExpenses(expenses)
        )
    }

    private fun splitAndSortExpenses(expenses: List<ReducedExpenseDto>): List<ReducedExpenseDto> {
        val abos = mutableListOf<ReducedExpenseDto>()
        val normal = mutableListOf<ReducedExpenseDto>()

        expenses.forEach {
            if (it.recurring != null) {
                abos.add(it)
            } else {
                normal.add(it)
            }
        }

        abos.sortBy { it.expenseDate }
        normal.sortBy { it.expenseDate }
        val sortedAbos = abos.reversed()
        val sortedNormal = normal.reversed()

        return sortedAbos + sortedNormal
    }

    fun updateExpense(updated: ExpenseDto, wgId: UUID, forFuture: Boolean? = false): ExpenseDto {
        val expense = this.findById(updated.id!!)

        this.invoicePeriodService.checkPeriodIsEditable(expense.periodId, wgId)

        expense.description = updated.description
        expense.image = updated.image ?: expense.image
        expense.price = updated.price
        expense.recurring = updated.recurring ?: expense.recurring

        val savedExpense = this.expenseRepository.saveAndFlush(expense)
        if (forFuture == true && savedExpense.aboId != null && savedExpense.recurring != null) {
            this.aboService.updateAboFromExpense(savedExpense)
        }

        return savedExpense.toDto()
    }

    fun createExpense(new: ExpenseDto, wgId: UUID): ExpenseDto {
        val currentPeriod = this.invoicePeriodService.getCurrentInvoicePeriod(wgId)
        val aboId = when (new.recurring) {
            null -> null
            else -> this.aboService.createAboFromExpense(new, wgId)
        }
        val newExpense = Expense(
            description = new.description,
            price = new.price,
            payer = this.userRepository.getReferenceById(AuthUtil.getCurrentUserId()),
            image = new.image,
            recurring = new.recurring,
            periodId = currentPeriod.id,
            aboId = aboId
        )

        this.notificationService.sendNewExpenseNotification(newExpense, wgId)

        return this.expenseRepository.saveAndFlush(newExpense).toDto()
    }

    fun deleteExpenseById(expenseId: UUID, wgId: UUID, forFuture: Boolean = false): String {
        val expense = this.findById(expenseId)

        this.invoicePeriodService.checkPeriodIsEditable(expense.periodId, wgId)

        val aboId = expense.aboId
        if (forFuture && aboId !== null && expense.recurring != null) {
            this.aboService.deleteAboById(aboId)
        }
        this.expenseRepository.delete(expense)
        return expense.description
    }

    fun createRecurringExpenses(recurring: Recurring) {
        val allCurrentPeriods = this.invoicePeriodService.getAllCurrentInvoicePeriods()
        val distinctPeriods = allCurrentPeriods.distinctBy { it.wgId }

        if (allCurrentPeriods.size != distinctPeriods.size) {
            val message =
                "At least one Wg with more than one invoice period without endDate! --> More than one open invoice!"
            throw InvalidPropertiesFormatException(message)
        }

        val newExpensesToSave = mutableListOf<Expense>()
        allCurrentPeriods.forEach {
            val newExpenses = this.aboService.buildExpensesByRecurringAndWgIdAndPeriodId(recurring, it)
            newExpensesToSave.addAll(newExpenses)
        }

        this.expenseRepository.saveAllAndFlush(newExpensesToSave)
    }

    fun calculateTotalExpenseByUser(periodId: UUID): InvoiceStatusByUserDto {
        val invoicePeriod = this.invoicePeriodService.getInvoicePeriodById(periodId)
        val flatMates = this.wgUserService.getFlatMatesByWgId(invoicePeriod.wgId)
        val expenses = this.expenseRepository.findExpensesByPeriodId(periodId)
        val totalSum = this.calculateTotalSum(expenses)
        val average = BigDecimal.valueOf(this.calculateAverage(flatMates, expenses))
        val currentUser = this.userService.getCurrentUser().toReducedDto()
        val contribution = this.calculateContributionByUserId(expenses, AuthUtil.getCurrentUserId())
        val deviation: BigDecimal = contribution - average

        return InvoiceStatusByUserDto(
            user = currentUser,
            invoicePeriod = invoicePeriod.toDto(),
            totalSum = ExpenseUtil.convertValidPrice(totalSum),
            averageAmount = ExpenseUtil.convertValidPrice(average.toDouble()),
            contributed = ExpenseUtil.convertValidPrice(contribution.toDouble()),
            deviation = ExpenseUtil.convertValidPrice(deviation.toDouble())
        )
    }

    fun calculateTotalExpensesOfAll(periodId: UUID): List<InvoiceStatusByUserDto> {
        val invoicePeriod = this.invoicePeriodService.getInvoicePeriodById(periodId)
        val flatMates = this.wgUserService.getFlatMatesByWgId(invoicePeriod.wgId)
        val expenses = this.expenseRepository.findExpensesByPeriodId(periodId)
        val totalSum = this.calculateTotalSum(expenses)
        val average = this.calculateAverage(flatMates, expenses)

        return flatMates.map {
            val contribution = this.calculateContributionByUserId(expenses, it.id).toDouble()

            InvoiceStatusByUserDto(
                user = it,
                invoicePeriod = invoicePeriod.toDto(),
                totalSum = ExpenseUtil.convertValidPrice(totalSum),
                averageAmount = ExpenseUtil.convertValidPrice(average),
                contributed = ExpenseUtil.convertValidPrice(contribution),
                deviation = ExpenseUtil.convertValidPrice(contribution - average)
            )
        }
    }

    private fun calculateContributionByUserId(expenses: List<Expense>, userId: UUID): BigDecimal {
        return (expenses.filter { exp -> exp.payer.id == userId }).sumOf { e -> BigDecimal.valueOf(e.price) }
    }


    private fun calculateAverage(flatMates: List<ReducedUserDto>, expenses: List<Expense>): Double {
        val matesCount = flatMates.size
        val totalSum = this.calculateTotalSum(expenses)
        return totalSum / matesCount
    }

    fun calculateDeviationByPeriodId(periodId: UUID): List<DeviationDto> {
        val invoicePeriod = this.invoicePeriodService.getInvoicePeriodById(periodId)
        val flatMates = this.wgUserService.getFlatMatesByWgId(invoicePeriod.wgId)
        val expenses = this.expenseRepository.findExpensesByPeriodId(periodId)
        val average = this.calculateAverage(flatMates, expenses)
        val deviationResult = flatMates.map { this.buildDeviationForUser(it, expenses, average) }

        FinanceUtil.validateDeviations(deviationResult)
        return deviationResult
    }

    private fun calculateTotalSum(expenses: List<Expense>): Double {
        return expenses.sumOf { it.price }
    }

    fun buildDeviationForUser(user: ReducedUserDto, expenses: List<Expense>, average: Double): DeviationDto {
        val contribution = this.calculateContributionByUserId(expenses, user.id).toDouble()
        return DeviationDto(
            user = user,
            deviation = BigDecimal.valueOf(contribution).subtract(BigDecimal.valueOf(average))
        )
    }

    fun uploadFile(uploadedFile: MultipartFile, expenseId: UUID) {
        val expense = this.expenseRepository.findById(expenseId)
        if (expense.isEmpty) {
            throw InvalidParameterException("No expense could be found for this expenseId ($expenseId)!")
        }
        expense.get().image = uploadedFile.bytes
        this.expenseRepository.saveAndFlush(expense.get())
    }
}