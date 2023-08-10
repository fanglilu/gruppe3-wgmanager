package msp.gruppe3.wgmanager.endpoints.utils


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import msp.gruppe3.wgmanager.endpoints.interceptors.AuthorizationInterceptor
import msp.gruppe3.wgmanager.endpoints.interceptors.RequestInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


const val baseURL = "http://msp-ws2223-3.dev.mobile.ifi.lmu.de/"

/**
 * Retrofit clients handles requests to the baseUrl.
 * A converter factory is used to convert JSON object to Java object
 * INFO: I used jackson because we already use jackson in backend so its the same
 *
 * @author Marcello Alte
 */
object RetrofitClient {

    private fun client(token: String?): OkHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(AuthorizationInterceptor(token))
        .addInterceptor(RequestInterceptor) // Debugging purpose
        .build()

    fun getInstance(token: String = ""): Retrofit {
        val mapper = ObjectMapper()
            .registerModule(JavaTimeModule())

        return Retrofit.Builder()
            .client(client(token))
            .baseUrl(baseURL)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
    }
}