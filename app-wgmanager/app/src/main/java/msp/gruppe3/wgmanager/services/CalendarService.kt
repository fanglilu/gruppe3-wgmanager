package msp.gruppe3.wgmanager.services

import msp.gruppe3.wgmanager.common.ErrorUtil.Companion.errorHandling
import msp.gruppe3.wgmanager.endpoints.apis.CalendarApi
import msp.gruppe3.wgmanager.endpoints.utils.RetrofitClient
import msp.gruppe3.wgmanager.models.CalendarEntry
import msp.gruppe3.wgmanager.models.dtos.CalendarEntryCreateDto
import msp.gruppe3.wgmanager.models.dtos.MessageDto
import okhttp3.ResponseBody
import java.util.*


private const val TAG = "Calendar Service"


class CalendarService(token: String) {

    private val retrofit = RetrofitClient.getInstance(token)
    private val calendarApi = retrofit.create(CalendarApi::class.java)

    suspend fun createCalendarEntry(calendar: CalendarEntryCreateDto): CalendarEntryCreateDto? {
        val response = calendarApi.createCalendarEntry(calendar)


        println(response.message())

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun getCalendarEntries(wgId: String): MutableMap<String, MutableList<CalendarEntry>>? {
        val response = calendarApi.getCalendarEntriesOfWg(wgId)
        println(response.message())

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }


    suspend fun getCalendarEntry(calendarId: UUID): CalendarEntry? {
        val response = calendarApi.getCalendarEntry(calendarId)
        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun updateCalendarEntry(
        calendarId: UUID,
        updatedCalendarEntryCreateDto: CalendarEntryCreateDto
    ): CalendarEntry? {
        val response = calendarApi.updateCalendarEntry(calendarId, updatedCalendarEntryCreateDto)
        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }


    suspend fun deleteCalendarEntry(
        calendarId: UUID
    ): MessageDto? {
        val response = calendarApi.deleteCalendarEntry(calendarId)
        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }
}