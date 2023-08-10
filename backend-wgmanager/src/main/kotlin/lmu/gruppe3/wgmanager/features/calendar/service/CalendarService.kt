package lmu.gruppe3.wgmanager.features.calendar.service

import lmu.gruppe3.wgmanager.common.dto.MessageDto
import lmu.gruppe3.wgmanager.features.calendar.domain.CalendarEntry
import lmu.gruppe3.wgmanager.features.calendar.dto.CalendarCreateEntryDto
import lmu.gruppe3.wgmanager.features.calendar.dto.CalendarEntryDto
import lmu.gruppe3.wgmanager.features.calendar.repository.CalendarEntryRepository
import lmu.gruppe3.wgmanager.notifications.dto.CreateNotificationDto
import lmu.gruppe3.wgmanager.notifications.repository.NotificationRepository
import lmu.gruppe3.wgmanager.notifications.service.NotificationService
import lmu.gruppe3.wgmanager.user.dto.ReducedUserDto
import lmu.gruppe3.wgmanager.user.repository.UserRepository
import lmu.gruppe3.wgmanager.userOnCalendarEntry.domain.UserOnCalendarEntry
import lmu.gruppe3.wgmanager.userOnCalendarEntry.repository.UserOnCalendarRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

@Service
class CalendarService(
    private val calendarRepository: CalendarEntryRepository,
    private val userOnCalendarRepository: UserOnCalendarRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) {
    var logger: Logger = LoggerFactory.getLogger(CalendarService::class.java)


    private fun findAllCalenderEntriesByWgId(wgId: UUID): List<CalendarEntryDto> {
        val result = calendarRepository.findAllCalenderEntriesByWgId(wgId).map {
            it.toDto()
        }
        result.map { calendarEntryDto ->
            calendarEntryDto.userOnCalendarList =
                this.userOnCalendarRepository.findAllUserById(calendarEntryDto.id)
                    .map { it.toUserIdDto() }.toMutableSet()
        }
        return result
    }

    fun findAllCalenderEntriesByWgIdGroupedByDate(wgId: UUID): MutableMap<String, MutableList<CalendarEntryDto>>? {
        val entries = findAllCalenderEntriesByWgId(wgId)
            .stream()
            .collect(
                Collectors.groupingBy {
                    it.date
                })

        println(entries)
        this.logger.info("Calendar was requested by Wg with ID: $wgId")

        return entries
    }

    fun createCalendarEntry(calendarCreateEntryDto: CalendarCreateEntryDto): CalendarEntryDto {

        val newCalendarEntry = CalendarEntry(
            wgId = calendarCreateEntryDto.wgId,
            creator = calendarCreateEntryDto.creator,
            title = calendarCreateEntryDto.title,
            date = calendarCreateEntryDto.date,
            startTime = calendarCreateEntryDto.startTime,
            endingTime = calendarCreateEntryDto.endingTime,
            description = calendarCreateEntryDto.description,
            endingDate = calendarCreateEntryDto.endingDate
        )


        val savedCalendarEntry = this.calendarRepository.saveAndFlush(newCalendarEntry)
        val userOnCalendarSet: MutableSet<UserOnCalendarEntry> = mutableSetOf()
        var usersToNotificate = mutableSetOf<UUID>()
        usersToNotificate.add(calendarCreateEntryDto.creator)
        for (user: ReducedUserDto in calendarCreateEntryDto.userOnCalendarList) {
            userOnCalendarSet.add(
                UserOnCalendarEntry(
                    savedCalendarEntry,
                    userRepository.findById(user.id).orElseThrow()
                )
            )
            usersToNotificate.add(user.id)
        }
        val savedUsers = this.userOnCalendarRepository.saveAllAndFlush(userOnCalendarSet)
        this.logger.info("Calendar entry was created. ID: ${savedCalendarEntry.id} with users: ${savedUsers.map { it.toUserIdDto().id }}")

        val notificationService = NotificationService(notificationRepository)
        for (userId: UUID in usersToNotificate) {
            notificationService.createNotification(
                CreateNotificationDto(
                    recipient = userId,
                    title = newCalendarEntry.title,
                    content = "${newCalendarEntry.title} starts at ${
                        newCalendarEntry.startTime.subSequence(0, 5)
                    }",
                    dateToSend = LocalDateTime.parse(newCalendarEntry.date + "T" + newCalendarEntry.startTime)
                        .minusHours(3)
                )
            )
        }

        val response = savedCalendarEntry.toDto()
        response.userOnCalendarList = savedUsers.map { it.toUserIdDto() }.toMutableSet()
        return response

    }

    fun updateCalendarEntry(calendarEntryId: UUID, patchData: CalendarCreateEntryDto): CalendarEntryDto {

        var calendarEntry = this.calendarRepository.findById(calendarEntryId).get()

        val notificationService = NotificationService(notificationRepository)


        calendarEntry.date = patchData.date
        calendarEntry.endingDate = patchData.endingDate
        calendarEntry.startTime = patchData.startTime
        calendarEntry.endingTime = patchData.endingTime
        calendarEntry.title = patchData.title
        calendarEntry.description = patchData.description


        calendarEntry.userOnCalendarList.clear()
        var usersToNotificate = mutableSetOf<UUID>()

        calendarEntry.userOnCalendarList.addAll(patchData.userOnCalendarList.map {
            UserOnCalendarEntry(
                calendar = calendarEntry,
                user = userRepository.findById(it.id).orElseThrow()
            )

        }.toMutableSet())

        deleteNotification(calendarEntry, notificationService)
        usersToNotificate.addAll(calendarEntry.userOnCalendarList.map { it.user.id })

        for (userId: UUID in usersToNotificate) {
            notificationService.createNotification(
                CreateNotificationDto(
                    recipient = userId,
                    title = calendarEntry.title,
                    content = "${calendarEntry.title} starts at ${calendarEntry.startTime.subSequence(0, 5)}",
                    dateToSend = LocalDateTime.parse(calendarEntry.date + "T" + calendarEntry.startTime)
                        .minusHours(3)
                )
            )
        }

        this.calendarRepository.save(calendarEntry)

        println(this.calendarRepository.findById(calendarEntry.id).get().toDto())


        return calendarEntry.toDto()
    }


    fun getCalendarEntry(calendarEntryId: UUID): CalendarEntryDto {
        val result = this.calendarRepository.findById(calendarEntryId).orElseThrow().toDto()
        result.userOnCalendarList =
            this.userOnCalendarRepository.findAllUserById(calendarEntryId).map { it.toUserIdDto() }.toMutableSet()
        this.logger.info("Calendar entry with id $calendarEntryId was requested")
        return result
    }


    fun deleteCalendarEntry(calendarEntryId: UUID): MessageDto {
        val calendarEntryToDelete = this.calendarRepository.findById(calendarEntryId).get()
        calendarEntryToDelete.userOnCalendarList.clear()
        this.userOnCalendarRepository.deleteAllById(this.userOnCalendarRepository.findAllKeysForEntry(calendarEntryId))
        this.calendarRepository.deleteById(calendarEntryId)
        this.logger.info(" Permanently deleted calendar entry with ID: $calendarEntryId")
        val notificationService = NotificationService(notificationRepository)
        deleteNotification(calendarEntryToDelete, notificationService)


        return MessageDto("Success!")
    }

    fun deleteNotification(calendarEntry: CalendarEntry, notificationService: NotificationService) {
        println("${calendarEntry.title} starts at ${calendarEntry.startTime.subSequence(0, 5)}")
        notificationService
            .deleteNotificationByContent(
                "${calendarEntry.title} starts at ${
                    calendarEntry.startTime.subSequence(
                        0,
                        5
                    )
                }"
            )
    }


}