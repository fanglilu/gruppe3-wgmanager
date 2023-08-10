package msp.gruppe3.wgmanager.endpoints.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response


// Tag for logging
private const val TAG = "Request Interceptor"

/**
 * This interceptor is used for logging purposes
 *
 * @author Marcello Alte
 */
object RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.e(TAG, "Outgoing request url ${request.url()} : Body ${request.body()}")
        return chain.proceed(request)
    }
}