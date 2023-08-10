
package msp.gruppe3.wgmanager.endpoints.apis

import msp.gruppe3.wgmanager.models.Notification
import msp.gruppe3.wgmanager.models.dtos.GetNotificationDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

const val subURLNotificationRoot = "notification"

interface NotificationApi {
    @GET("/$subURLNotificationRoot/getNotifications")
    suspend fun getNotifications(@Body notification: GetNotificationDto): Response<List<Notification>>
    @GET("/$subURLNotificationRoot/getNotifications/{requester}")
    suspend fun getNotifications(@Path("requester") requester: UUID): Response<List<Notification>>
}