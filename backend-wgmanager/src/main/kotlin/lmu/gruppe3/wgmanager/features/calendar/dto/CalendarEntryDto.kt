package lmu.gruppe3.wgmanager.features.calendar.dto

import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import java.util.UUID

data class CalendarEntryDto(
    var id: UUID,
    var creator: UUID,
    var wgId: UUID,
    var title: String,
    var date: String,
    var startTime: String,
    var endingTime: String,
    var description: String,
    var endingDate: String,
    var userOnCalendarList: MutableSet<ReducedUserDto> = mutableSetOf()
)