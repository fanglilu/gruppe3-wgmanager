package lmu.gruppe3.wgmanager.notifications.repository

import lmu.gruppe3.wgmanager.notifications.domain.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface NotificationRepository : JpaRepository<Notification, UUID> {
    @Query("select * from notifications where date_to_send < :dateNow and recipient = :recipient", nativeQuery = true)
    fun findByRecipientAndDateToSend(
        @Param("recipient") recipient: UUID,
        @Param("dateNow") dateNow: LocalDateTime
    ): List<Notification>?


    @Query("select *  from notifications where content = :content", nativeQuery = true)
    fun findNotificationsByContent(@Param("content") content: String): List<Notification>?

}