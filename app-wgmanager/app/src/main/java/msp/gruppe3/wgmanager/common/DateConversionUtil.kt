package msp.gruppe3.wgmanager.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class DateConversionUtil {
    fun convertCalendarToLocalTime(calendar: Calendar): LocalDateTime {
        return LocalDateTime.ofInstant(
            calendar.toInstant(),
            calendar.timeZone.toZoneId()
        )
    }

    fun convertLocalTimeToCalendar(localDate: LocalDate): Calendar {
        val zoneId: ZoneId = ZoneId.systemDefault()
        val date = Date.from(localDate.atStartOfDay(zoneId).toInstant())
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }
}