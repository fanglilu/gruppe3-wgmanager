package lmu.gruppe3.wgmanager.notifications.domain

import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.notifications.dto.NotificationDto
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "notifications")
data class Notification (

    var title: String,
    var recipient: UUID,
    var content: String,
    var dateToSend: LocalDateTime,

    ) : BaseEntity() {
    fun toDto(): NotificationDto {
        return NotificationDto(
            title = this.title,
            content = this.content,
        )
    }
}