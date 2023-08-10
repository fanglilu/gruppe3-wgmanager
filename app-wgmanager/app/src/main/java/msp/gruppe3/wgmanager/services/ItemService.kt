package msp.gruppe3.wgmanager.services

import msp.gruppe3.wgmanager.common.ErrorUtil.Companion.errorHandling
import msp.gruppe3.wgmanager.endpoints.apis.ItemApi
import msp.gruppe3.wgmanager.endpoints.utils.RetrofitClient
import msp.gruppe3.wgmanager.models.Item
import msp.gruppe3.wgmanager.models.dtos.DeleteItemDto
import msp.gruppe3.wgmanager.models.dtos.RegisterItemDto
import msp.gruppe3.wgmanager.models.dtos.UpdateItemDto
import okhttp3.ResponseBody
import java.util.*

private const val TAG = "Item Service"

class ItemsService(token: String) {

    private val retrofit = RetrofitClient.getInstance(token)
    private val itemApi = retrofit.create(ItemApi::class.java)

    suspend fun getItems(listID: UUID): List<Item>? {
        val response = itemApi.getItems(listID)
        if (response.isSuccessful == false) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }
        return response.body()
    }

    suspend fun getMultipleItems(listID: List<UUID>): List<Item>? {
        val response = itemApi.getMultipleItems(listID)
        if (response.isSuccessful == false) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }

        return response.body()
    }

    suspend fun createItem(createItemDto: RegisterItemDto): List<Item>? {
        val response = itemApi.createItem(createItemDto)
        if (response.isSuccessful == false) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }
        return response.body()
    }

    suspend fun updateItem(updateItemDto: UpdateItemDto): List<Item>? {
        val response = itemApi.updateItem(updateItemDto)
        if (response.isSuccessful == false) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }
        return response.body()
    }

    suspend fun deleteItem(deleteItemDto: DeleteItemDto): Boolean? {
        val response = itemApi.deleteItem(deleteItemDto)
        if (response.isSuccessful == false) {
            val errorBody: ResponseBody? = response.errorBody()
            if (errorBody != null) {
                errorHandling(errorBody, TAG)
            }
        }
        return response.body()
    }
}
