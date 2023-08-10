package lmu.gruppe3.wgmanager.features.calendar.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import lmu.gruppe3.wgmanager.common.domain.BaseEntity
import lmu.gruppe3.wgmanager.features.calendar.dto.CalendarEntryDto
import lmu.gruppe3.wgmanager.userOnCalendarEntry.domain.UserOnCalendarEntry
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table


@Entity
@Table(name = "calendar_entry")
data class CalendarEntry(
    var wgId: UUID,
    var creator: UUID,
    var title: String,
    var date: String,
    var startTime: String,
    var endingTime: String,
    var description: String,
    var endingDate: String

) : BaseEntity() {

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var userOnCalendarList: MutableSet<UserOnCalendarEntry> = mutableSetOf()

    fun toDto(): CalendarEntryDto {
        return CalendarEntryDto(
            id = this.id,
            creator = this.creator,
            wgId = this.wgId,
            title = this.title,
            date = this.date,
            startTime = this.startTime,
            endingTime = this.endingTime,
            description = this.description,
            endingDate = this.endingDate,
            userOnCalendarList = this.userOnCalendarList.map { it.user.toReducedUserDto() }.toMutableSet()
        )

    }
}
