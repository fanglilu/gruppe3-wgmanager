package lmu.gruppe3.wgmanager.userOnCalendarEntry.repository

import lmu.gruppe3.wgmanager.userOnCalendarEntry.domain.UserCalendarKey
import lmu.gruppe3.wgmanager.userOnCalendarEntry.domain.UserOnCalendarEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserOnCalendarRepository : JpaRepository<UserOnCalendarEntry, UserCalendarKey> {
    @Query("select c from UserOnCalendarEntry c where c.calendar.id = :calenderEntryId")
    fun findAllUserById(calenderEntryId: UUID): List<UserOnCalendarEntry>


    @Query("select c.id from UserOnCalendarEntry c where c.calendar.id = :calenderEntryId")
    fun findAllKeysForEntry(calenderEntryId: UUID): List<UserCalendarKey>

}