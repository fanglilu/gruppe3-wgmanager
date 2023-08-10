package msp.gruppe3.wgmanager.services

import msp.gruppe3.wgmanager.common.ErrorUtil.Companion.errorHandling
import msp.gruppe3.wgmanager.endpoints.apis.NotificationApi
import msp.gruppe3.wgmanager.endpoints.utils.RetrofitClient
import msp.gruppe3.wgmanager.models.Notification
import okhttp3.ResponseBody
import java.util.*

private const val TAG = "Notification Service"

class NotificationService {

    private val retrofit = RetrofitClient.getInstance()
    private val notificationApi = retrofit.create(NotificationApi::class.java)

    suspend fun getNotifications(reqester: UUID): List<Notification>? {
        val response = notificationApi.getNotifications(reqester)
        if (response.isSuccessful == false) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }
}
