package lmu.gruppe3.wgmanager.common.jobs

import lmu.gruppe3.wgmanager.finance.service.DebtService
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class CashCrashJob(
    private val debtService: DebtService
) {
    @Scheduled(cron = "0 0 0 1 * *")
    fun endCurrentPeriodAndCreateNewPeriod() {
        this.debtService.endPeriodAndCreateNextPeriod()
    }
}