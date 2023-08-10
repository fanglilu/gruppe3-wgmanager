package msp.gruppe3.wgmanager

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.models.Wg
import msp.gruppe3.wgmanager.models.dtos.RoleWgDto
import msp.gruppe3.wgmanager.models.dtos.UserDto
import msp.gruppe3.wgmanager.services.UserService
import msp.gruppe3.wgmanager.services.WgService
import java.util.*


// Tag for logging
private const val TAG = "Main View Model"

class MainViewModel : ViewModel() {
    // WgDetailFragment will display this one
    val wgCurrent: MutableLiveData<Wg> by lazy { MutableLiveData<Wg>() }
    // List of all wg´s a user joined
    val wgList = MutableLiveData<List<RoleWgDto>>()

    val userCurrent: MutableLiveData<UserDto> by lazy { MutableLiveData<UserDto>() }


    fun setWg(wg: UUID, token: String) {
        val wgService = WgService(token)
        CoroutineScope(Dispatchers.Main).launch {
            val response = wgService.getById(wg)
            wgCurrent.postValue(response)
            Log.d(TAG, "Set current wg to: $wgCurrent")
        }
    }

    fun updateWgList(activity: Activity) {
        val token = TokenUtil.getTokenByActivity(activity)
        val userService = UserService(token)
        CoroutineScope(Dispatchers.Main).launch {
            val response = userService.findMe()
            Log.d(TAG, "updateWgList $response")
            if (response?.wgList?.isNotEmpty() == true) {
                wgList.value = response.wgList
                if (wgCurrent.value == null) {
                    val wgId: UUID = response.wgList[0].id
                    setWg(wgId, token)
                }
            } else {
                wgList.value = emptyList()
                wgCurrent.value = null
            }
        }
    }

    fun setUser(user: UserDto?) {
        userCurrent.postValue(user)
    }
}