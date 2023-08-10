package msp.gruppe3.wgmanager.services

import msp.gruppe3.wgmanager.common.ErrorUtil
import msp.gruppe3.wgmanager.endpoints.apis.WgApi
import msp.gruppe3.wgmanager.endpoints.utils.RetrofitClient
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.SuccessDto
import msp.gruppe3.wgmanager.models.dtos.WgCreateDto
import okhttp3.ResponseBody
import java.util.*


// Tag for logging
private const val TAG = "Wg Service"

/**
 * Service for API calls via Retrofit 2 and includes error handling
 *
 * @author Marcello Alte
 */
class WgService(token: String) {

    private val retrofit = RetrofitClient.getInstance(token)
    private val wgApi = retrofit.create(WgApi::class.java)

    suspend fun joinWg(invitationCode: String): Wg? {
        val response = wgApi.joinWg(invitationCode)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun createWg(wg: WgCreateDto): Wg? {
        val response = wgApi.createWg(wg)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }
        return response.body()
    }

    suspend fun getInvitationCodeWg(id: UUID): String? {
        val response = wgApi.invitationCode(id)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()?.invitationCode
    }

    suspend fun getById(id: UUID): Wg? {
        val response = wgApi.getWgById(id)
        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun deleteWg(id: UUID): SuccessDto? {
        val response = wgApi.deleteWg(id)
        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun leaveWg(id: UUID): SuccessDto? {
        val response = wgApi.leaveWg(id)
        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                ErrorUtil.errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }
}