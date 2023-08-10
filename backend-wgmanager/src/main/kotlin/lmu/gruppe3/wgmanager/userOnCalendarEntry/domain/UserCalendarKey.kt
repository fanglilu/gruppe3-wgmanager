package lmu.gruppe3.wgmanager.userOnCalendarEntry.domain

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Embeddable
data class UserCalendarKey(
    @Column(name = "calendar_id")
    val calendarEntryId: UUID,
    @Column(name = "user_id")
    val userId: UUID
) : Serializable {}
