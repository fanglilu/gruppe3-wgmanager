package msp.gruppe3.wgmanager.models

import com.fasterxml.jackson.annotation.JsonProperty
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto
import java.util.*

data class CalendarEntry(
    @JsonProperty("id")
    var id: UUID,
    @JsonProperty("wgId")
    var wgId: UUID,
    @JsonProperty("creator")
    var creator: UUID,
    @JsonProperty("title")
    var title: String,
    @JsonProperty("date")
    var date: String,
    @JsonProperty("startTime")
    var startTime: String,
    @JsonProperty("endingDate")
    var endingDate: String,
    @JsonProperty("endingTime")
    var endingTime: String,
    @JsonProperty("description")
    var description: String,
    @JsonProperty("userOnCalendarList", required = false)
    var userOnCalendarList: List<ReducedUserDto>
)
