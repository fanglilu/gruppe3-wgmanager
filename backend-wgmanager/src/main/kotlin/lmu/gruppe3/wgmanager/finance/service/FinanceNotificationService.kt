package lmu.gruppe3.wgmanager.finance.service

import lmu.gruppe3.wgmanager.common.util.AuthUtil
import lmu.gruppe3.wgmanager.common.util.ExpenseUtil
import lmu.gruppe3.wgmanager.finance.domain.Debt
import lmu.gruppe3.wgmanager.finance.domain.Expense
import lmu.gruppe3.wgmanager.finance.dto.InvoiceStatusByUserDto
import lmu.gruppe3.wgmanager.notifications.dto.CreateNotificationDto
import lmu.gruppe3.wgmanager.notifications.service.NotificationService
import lmu.gruppe3.wgmanager.wg_user.service.WgUserService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class FinanceNotificationService(
    private val notificationService: NotificationService,
    private val wgUserService: WgUserService
) {


    fun sendNewExpenseNotification(expense: Expense, wgId: UUID) {
        val currentUserId = AuthUtil.getCurrentUserId()
        val flatMates = this.wgUserService.getFlatMatesByWgId(wgId).filter { it.id != currentUserId }
        val formattedPrice = ExpenseUtil.convertValidPrice(expense.price)

        val message = "${expense.description} - ${formattedPrice}€ has been added by ${expense.payer.name}"
        flatMates.forEach {
            this.notificationService.createNotification(
                CreateNotificationDto(
                    recipient = it.id,
                    title = "New Expense",
                    content = message,
                    dateToSend = LocalDateTime.now()
                )
            )
        }
    }

    fun sendCashCrashNotification(flatmateStatusList: List<InvoiceStatusByUserDto>) {
        flatmateStatusList.forEach {

            val formattedTotal = ExpenseUtil.convertValidPrice(it.totalSum)
            val formattedDeviation = ExpenseUtil.convertValidPrice(it.deviation)
            val message = "A total of ${formattedTotal}€ was spent and your deviation is ${formattedDeviation}€"

            this.notificationService.createNotification(
                CreateNotificationDto(
                    recipient = it.user.id,
                    title = "CashCrash",
                    content = message,
                    dateToSend = LocalDateTime.now()
                )
            )
        }

    }

    fun sendSettleUpNotification(settledDebts: List<Debt>) {
        settledDebts.forEach {
            val formattedDebt = ExpenseUtil.convertValidPrice(it.debt)
            val message = "${it.debtor.name} has payed ${formattedDebt}€"
            this.notificationService.createNotification(
                CreateNotificationDto(
                    recipient = it.receiver.id,
                    title = "${it.debtor.name} setted up",
                    content = message,
                    dateToSend = LocalDateTime.now()
                )
            )
        }
    }

}