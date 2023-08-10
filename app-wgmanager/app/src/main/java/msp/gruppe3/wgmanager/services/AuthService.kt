package msp.gruppe3.wgmanager.services

import msp.gruppe3.wgmanager.common.ErrorUtil
import msp.gruppe3.wgmanager.endpoints.apis.AuthApi
import msp.gruppe3.wgmanager.endpoints.utils.RetrofitClient
import msp.gruppe3.wgmanager.models.dtos.LoginDto
import msp.gruppe3.wgmanager.models.dtos.RegisterDto
import msp.gruppe3.wgmanager.models.dtos.TokenDto
import msp.gruppe3.wgmanager.models.dtos.UserDto
import okhttp3.ResponseBody

private const val TAG = "AuthService"

class AuthService {
    private val retrofit = RetrofitClient.getInstance()
    private val authApi = retrofit.create(AuthApi::class.java)

    suspend fun login(login: LoginDto): TokenDto? {
        val response = authApi.login(login)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun register(registerDto: RegisterDto): UserDto? {
        val response = authApi.register(registerDto)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

}