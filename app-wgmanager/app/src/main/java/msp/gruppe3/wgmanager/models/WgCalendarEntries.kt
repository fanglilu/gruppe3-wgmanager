package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.models.CalendarEntry
import java.time.LocalDateTime
import java.util.*

data class WgCalendarEntries(
    @JsonProperty("id")
    var id: UUID,
    @JsonProperty("wgId")
    var wgId: UUID,
    @JsonProperty("userId")
    var userId: UUID,
    @JsonProperty("calendarList")
    var calendarList: Map<LocalDateTime, List<CalendarEntry>>
)
