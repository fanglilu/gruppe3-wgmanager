package msp.gruppe3.wgmanager.endpoints.apis

import msp.gruppe3.wgmanager.models.ShoppingListEntry
import msp.gruppe3.wgmanager.models.dtos.GetListDto
import msp.gruppe3.wgmanager.models.dtos.RegisterListDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*

const val subURLList = "list"

interface ListApi {
    @POST("/$subURLList/newList")
    suspend fun createNewList(@Body list: RegisterListDto): Response<List<ShoppingListEntry>>

    @POST("/$subURLList/getMyLists")
    suspend fun getMyLists(@Body listsToGet: GetListDto): Response<List<ShoppingListEntry>>

    @DELETE("/$subURLList/delete/{id}")
    suspend fun deleteList(@Path("id") id: UUID)
}
