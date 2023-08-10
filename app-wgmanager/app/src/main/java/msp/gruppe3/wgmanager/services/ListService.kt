package msp.gruppe3.wgmanager.services

import msp.gruppe3.wgmanager.common.ErrorUtil.Companion.errorHandling
import msp.gruppe3.wgmanager.endpoints.apis.ListApi
import msp.gruppe3.wgmanager.endpoints.utils.RetrofitClient
import msp.gruppe3.wgmanager.models.ShoppingListEntry
import msp.gruppe3.wgmanager.models.dtos.GetListDto
import msp.gruppe3.wgmanager.models.dtos.RegisterListDto
import okhttp3.ResponseBody
import java.util.*

private const val TAG = "Item Service"

class ListService(token:String) {

    private val retrofit = RetrofitClient.getInstance(token)
    private val listApi = retrofit.create(ListApi::class.java)

    suspend fun createList(list: RegisterListDto): List<ShoppingListEntry>? {
        val response = listApi.createNewList(list)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }
        return response.body()
    }

    suspend fun findMyLists(wgWithLists: GetListDto): List<ShoppingListEntry>? {
        val response = listApi.getMyLists(wgWithLists)

        if (!response.isSuccessful) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }
        return response.body()
    }

    suspend fun deleteList(listToDelete: UUID) {
       listApi.deleteList(listToDelete)
    }
}