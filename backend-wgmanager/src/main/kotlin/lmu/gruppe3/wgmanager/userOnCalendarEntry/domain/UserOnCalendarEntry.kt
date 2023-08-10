package lmu.gruppe3.wgmanager.userOnCalendarEntry.domain

import lmu.gruppe3.wgmanager.features.calendar.domain.CalendarEntry
import lmu.gruppe3.wgmanager.user.domain.User
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import javax.persistence.*

@Entity
data class UserOnCalendarEntry(
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("calendarId")
    @JoinColumn(name = "calendar_id")
    val calendar: CalendarEntry,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    val user: User,

    ) {
    @EmbeddedId
    private var id: UserCalendarKey = UserCalendarKey(this.calendar.id, this.user.id)

    fun toUserIdDto(): ReducedUserDto {
        return this.user.toReducedUserDto()
    }



}
