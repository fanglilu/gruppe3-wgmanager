package msp.gruppe3.wgmanager.services

import msp.gruppe3.wgmanager.common.ErrorUtil
import msp.gruppe3.wgmanager.endpoints.apis.UserApi
import msp.gruppe3.wgmanager.endpoints.utils.RetrofitClient
import msp.gruppe3.wgmanager.models.dtos.UserDto
import okhttp3.ResponseBody


private const val TAG = "UserService"

class UserService(token: String) {
    private val retrofit =
        RetrofitClient.getInstance(token)
    private val userApi = retrofit.create(UserApi::class.java)

    suspend fun findMe(): UserDto? {
        val response = userApi.findMe()

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                //ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

}