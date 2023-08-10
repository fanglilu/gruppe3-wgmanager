package lmu.gruppe3.wgmanager.common.jobs

import lmu.gruppe3.wgmanager.finance.enum.Recurring
import lmu.gruppe3.wgmanager.finance.service.ExpenseService
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class AboExpenseJob(
    private val expenseService: ExpenseService
) {
    @Scheduled(cron = "0 0 0 1 * *")
    fun createMonthlyExpenses() {
        this.expenseService.createRecurringExpenses(Recurring.MONTHLY)
    }

    @Scheduled(cron = "0 0 0 * * MON")
    fun createWeeklyExpenses() {
        this.expenseService.createRecurringExpenses(Recurring.WEEKLY)
    }
}