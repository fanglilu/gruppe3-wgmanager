package msp.gruppe3.wgmanager.endpoints.apis


import msp.gruppe3.wgmanager.models.CalendarEntry
import msp.gruppe3.wgmanager.models.dtos.CalendarEntryCreateDto
import msp.gruppe3.wgmanager.models.dtos.MessageDto
import retrofit2.Response
import retrofit2.http.*
import java.util.*


private const val subURLCalendar = "features/calendar"

interface CalendarApi {

    @POST("/$subURLCalendar/createCalendarEntry")
    suspend fun createCalendarEntry(@Body wg: CalendarEntryCreateDto): Response<CalendarEntryCreateDto>

    @GET("/$subURLCalendar/getCalendarEntriesOfWg/{id}")
    suspend fun getCalendarEntriesOfWg(@Path("id") id: String): Response<MutableMap<String, MutableList<CalendarEntry>>>

    @GET("/$subURLCalendar/getCalendarEntry/{id}")
    suspend fun getCalendarEntry(@Path("id") id: UUID): Response<CalendarEntry>

    @PATCH("$subURLCalendar/updateCalendarEntry/{id}")
    suspend fun updateCalendarEntry(
        @Path("id") id: UUID,
        @Body updatedCalendarEntry: CalendarEntryCreateDto
    ): Response<CalendarEntry>


    @DELETE("$subURLCalendar/deleteCalendarEntry/{id}")
    suspend fun deleteCalendarEntry(@Path("id") id: UUID): Response<MessageDto>

}