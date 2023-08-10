package msp.gruppe3.wgmanager.common

import android.util.Log
import okhttp3.ResponseBody

class ErrorUtil {

    companion object {
        fun errorHandling(errorBody: ResponseBody, tag: String) {
            Log.e(tag, errorBody.string())
        }
    }
}