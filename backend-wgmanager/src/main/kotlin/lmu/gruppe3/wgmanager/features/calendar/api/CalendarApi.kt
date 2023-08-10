package lmu.gruppe3.wgmanager.features.calendar.api

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import lmu.gruppe3.wgmanager.common.dto.MessageDto
import lmu.gruppe3.wgmanager.features.calendar.dto.CalendarCreateEntryDto
import lmu.gruppe3.wgmanager.features.calendar.dto.CalendarEntryDto
import lmu.gruppe3.wgmanager.features.calendar.service.CalendarService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/features/calendar")
class CalendarApi(private val calendarService: CalendarService) {

    @GetMapping("/getCalendarEntriesOfWg/{id}")
    fun getWgCalendarEntries(@PathVariable("id") id: UUID): ResponseEntity<MutableMap<String, MutableList<CalendarEntryDto>>> {
        val result = this.calendarService.findAllCalenderEntriesByWgIdGroupedByDate(id)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/createCalendarEntry")
    fun postWgCalendarEntry(@RequestBody calendarCreateEntryDto: CalendarCreateEntryDto): ResponseEntity<CalendarEntryDto> {
        val result = this.calendarService.createCalendarEntry(calendarCreateEntryDto)
        return ResponseEntity(result, HttpStatus.OK)
    }


    @PatchMapping("/updateCalendarEntry/{id}")
    fun updateCalendarEntry(
        @RequestBody patchData: CalendarCreateEntryDto,
        @PathVariable("id") id: UUID
    ): ResponseEntity<CalendarEntryDto> {
        val result = this.calendarService.updateCalendarEntry(id, patchData)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/getCalendarEntry/{id}")
    fun getCalendarEntryById(@PathVariable("id") id: UUID): ResponseEntity<CalendarEntryDto> {
        return ResponseEntity(this.calendarService.getCalendarEntry(id), HttpStatus.OK)
    }


    @DeleteMapping("/deleteCalendarEntry/{id}")
    fun deleteCalendarEntryById(@PathVariable("id") id: UUID): ResponseEntity<MessageDto> {
        val result = this.calendarService.deleteCalendarEntry(id)
        return ResponseEntity(result, HttpStatus.OK)
    }
}