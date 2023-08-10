package msp.gruppe3.wgmanager.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class CalendarEntryCreateDto(

    @JsonProperty("creator")
    var creator: UUID,
    @JsonProperty("id")
    var wgId: UUID,
    @JsonProperty("title")
    var title: String,
    @JsonProperty("date")
    var date: String,
    @JsonProperty("startTime")
    var startTime: String?,
    @JsonProperty("endingTime", required = false)
    var endingTime: String?,
    @JsonProperty("endingDate", required = false)
    var endingDate: String?,
    @JsonProperty("description", required = false)
    var description: String?,
    @JsonProperty("userOnCalendarList", required = false)
    var userOnCalendarList: List<ReducedUserDto?>?
)
