package lmu.gruppe3.wgmanager.notifications.dto

import java.time.LocalDateTime
import java.util.*

data class CreateNotificationDto(
    var recipient: UUID,
    var title: String,
    var content: String,
    var dateToSend: LocalDateTime,
)