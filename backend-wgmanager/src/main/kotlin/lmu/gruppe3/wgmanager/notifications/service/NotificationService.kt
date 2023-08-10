package lmu.gruppe3.wgmanager.notifications.service

import lmu.gruppe3.wgmanager.notifications.domain.Notification
import lmu.gruppe3.wgmanager.notifications.dto.CreateNotificationDto
import lmu.gruppe3.wgmanager.notifications.dto.NotificationDto
import lmu.gruppe3.wgmanager.notifications.repository.NotificationRepository
import lmu.gruppe3.wgmanager.wg_root.service.WgRootService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.time.LocalDateTime
import java.util.*

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
) {
    var logger: Logger = LoggerFactory.getLogger(WgRootService::class.java)

    fun getAll(): List<NotificationDto> {
        return this.notificationRepository.findAll().map(Notification::toDto)
    }

    fun getNotifications(id: UUID): List<NotificationDto> {
        val list = this.notificationRepository.findByRecipientAndDateToSend(id, LocalDateTime.now())
            ?: throw InvalidParameterException("recipient not found")
        for (notification in list) {
            notificationRepository.deleteById(notification.id)
        }
        return list.map(Notification::toDto)
    }

    fun createNotification(createNotificationDto: CreateNotificationDto) {
        if (createNotificationDto.title.isEmpty()) {
            throw InvalidParameterException("Provide a title")
        }
        if (createNotificationDto.content.isEmpty()) {
            throw InvalidParameterException("Provide content")
        }

        val newNotification = Notification(
            recipient = createNotificationDto.recipient,
            title = createNotificationDto.title,
            content = createNotificationDto.content,
            dateToSend = createNotificationDto.dateToSend
        )

        val savedNotification = this.notificationRepository.saveAndFlush(newNotification)
        this.logger.info("Notification was created. ID: ${newNotification.id}")
    }


    fun deleteNotificationByContent(content: String) {
        this.logger.info("Notification with content $content was deleted")

        val notifications = this.notificationRepository.findNotificationsByContent(content)
            ?: throw InvalidParameterException("Element not found")
        notifications.forEach { deleteNotification(it.id) }
    }

    fun deleteNotification(id: UUID) {
        this.notificationRepository.deleteById(id)
    }


}
