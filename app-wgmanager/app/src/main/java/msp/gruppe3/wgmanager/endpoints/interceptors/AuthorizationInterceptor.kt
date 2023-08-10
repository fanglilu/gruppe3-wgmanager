package msp.gruppe3.wgmanager.endpoints.interceptors

import okhttp3.Interceptor
import okhttp3.Response

/**
 * This class is used for adding authorization headers
 *
 * @author Marcello Alte
 */
class AuthorizationInterceptor(private val token: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithHeader = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(requestWithHeader)
    }
}