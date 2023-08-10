package msp.gruppe3.wgmanager.ui.features.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import msp.gruppe3.wgmanager.models.CalendarEntry
import msp.gruppe3.wgmanager.models.dtos.ReducedUserDto
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    private val userList: MutableLiveData<Set<ReducedUserDto>> by lazy { MutableLiveData<Set<ReducedUserDto>>() }

    private val calendarDataMap = MutableLiveData<Map<LocalDate, MutableList<CalendarEntry>>>()
    private val calendarDataMapModified =
        MutableLiveData<Map<LocalDate, MutableList<CalendarEntry>>>()

    private val calendarEntry = MutableLiveData<CalendarEntry>()


    fun addUsers(users: Set<ReducedUserDto>) {
        userList.postValue(users)
    }

    fun getUsers(): MutableLiveData<Set<ReducedUserDto>> {
        return this.userList
    }

    fun setCalendarDataMap(data: Map<LocalDate, MutableList<CalendarEntry>>) {
        this.calendarDataMap.postValue(data)
    }

    fun getCalendarDataMap(): MutableLiveData<Map<LocalDate, MutableList<CalendarEntry>>> {
        return this.calendarDataMap
    }

    fun setCalendarDataMapModified(data: Map<LocalDate, MutableList<CalendarEntry>>) {
        this.calendarDataMapModified.postValue(data)
    }

    fun getCalendarDataMapModified(): MutableLiveData<Map<LocalDate, MutableList<CalendarEntry>>> {
        return this.calendarDataMapModified
    }

    fun setCalendarEntry(calendarEntry: CalendarEntry) {
        this.calendarEntry.postValue(calendarEntry)
    }

    fun getCalendarEntry(): MutableLiveData<CalendarEntry> {
        return this.calendarEntry
    }

}