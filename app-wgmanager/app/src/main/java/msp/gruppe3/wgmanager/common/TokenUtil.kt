package msp.gruppe3.wgmanager.common

import android.app.Activity
import android.content.Context

class TokenUtil {

    companion object {

        fun getTokenByActivity(activity: Activity): String {
            return activity.getSharedPreferences("wgManagerPref", Context.MODE_PRIVATE)
                .getString("token", "") ?: ""
        }
    }
}