package lmu.gruppe3.wgmanager.features.calendar.repository

import lmu.gruppe3.wgmanager.features.calendar.domain.CalendarEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CalendarEntryRepository : JpaRepository<CalendarEntry, UUID> {
    @Query("select c from CalendarEntry c where c.wgId = :wgId")
    fun findAllCalenderEntriesByWgId(wgId: UUID): List<CalendarEntry>

}