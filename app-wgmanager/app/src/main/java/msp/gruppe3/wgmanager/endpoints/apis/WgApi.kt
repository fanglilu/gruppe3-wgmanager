package msp.gruppe3.wgmanager.endpoints.apis

import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.InvitationCodeDto
import msp.gruppe3.wgmanager.models.dtos.SuccessDto
import msp.gruppe3.wgmanager.models.dtos.WgCreateDto
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

private const val subURLWgRoot = "wg"

/**
 * Wg API routes are defined here
 *
 * @author Marcello Alte
 */
interface WgApi {

    @GET("/$subURLWgRoot/{id}/code")
    suspend fun invitationCode(@Path("id") id: UUID): Response<InvitationCodeDto>

    @GET("/$subURLWgRoot/join")
    suspend fun joinWg(@Query("code") code: String): Response<Wg>

    @GET("/$subURLWgRoot/{id}")
    suspend fun getWgById(@Path("id") id: UUID): Response<Wg>

    @POST("/$subURLWgRoot/create")
    suspend fun createWg(@Body wg: WgCreateDto): Response<Wg>

    @DELETE("/$subURLWgRoot/{id}")
    suspend fun deleteWg(@Path("id") id: UUID): Response<SuccessDto>

    @DELETE("/$subURLWgRoot/leaveWg/{id}")
    suspend fun leaveWg(@Path("id") id: UUID): Response<SuccessDto>
}