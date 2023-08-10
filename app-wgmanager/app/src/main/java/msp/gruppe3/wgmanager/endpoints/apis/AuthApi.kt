package msp.gruppe3.wgmanager.endpoints.apis

import msp.gruppe3.wgmanager.models.dtos.LoginDto
import msp.gruppe3.wgmanager.models.dtos.RegisterDto
import msp.gruppe3.wgmanager.models.dtos.TokenDto
import msp.gruppe3.wgmanager.models.dtos.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

private const val baseUrl = "auth"

interface AuthApi {
    @POST("/$baseUrl/login")
    suspend fun login(@Body login: LoginDto): Response<TokenDto>

    @POST("/$baseUrl/register")
    suspend fun register(@Body registerDto: RegisterDto): Response<UserDto>
}