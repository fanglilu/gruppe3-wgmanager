package msp.gruppe3.wgmanager.endpoints.apis

import msp.gruppe3.wgmanager.models.dtos.UserDto
import retrofit2.Response
import retrofit2.http.GET

private const val baseUrl = "user"

interface UserApi {
    @GET("/$baseUrl/me")
    suspend fun findMe(): Response<UserDto>
}