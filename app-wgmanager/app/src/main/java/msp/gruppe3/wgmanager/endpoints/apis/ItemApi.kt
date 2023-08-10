package msp.gruppe3.wgmanager.endpoints.apis

import msp.gruppe3.wgmanager.models.Item
import msp.gruppe3.wgmanager.models.dtos.DeleteItemDto
import msp.gruppe3.wgmanager.models.dtos.RegisterItemDto
import msp.gruppe3.wgmanager.models.dtos.UpdateItemDto
import retrofit2.Response
import retrofit2.http.*
import java.util.*

const val subURLItem = "item"

interface ItemApi {
    @GET("/$subURLItem/getItem/{id}")
    suspend fun getItems(@Path("id") id: UUID): Response<List<Item>>

    @POST("/$subURLItem/getMultipleItems")
    suspend fun getMultipleItems(@Body listOfUUID: List<UUID>): Response<List<Item>>

    @POST("/$subURLItem/newItem")
    suspend fun createItem(@Body createItemDto: RegisterItemDto): Response<List<Item>>

    @POST("/$subURLItem/updateItem")
    suspend fun updateItem(@Body updateItemDto: UpdateItemDto): Response<List<Item>>

    @POST("/$subURLItem/deleteItem")
    suspend fun deleteItem(@Body updateItemDto: DeleteItemDto): Response<Boolean>
}
