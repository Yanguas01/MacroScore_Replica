package es.upm.macroscore.data.network

import android.util.Log
import es.upm.macroscore.data.storage.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    /*
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${tokenManager.getAccessToken()}")
            .build()
        return chain.proceed(request)
    }*/

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${tokenManager.getAccessToken()}")
            .build()

        // Log the request
        val requestBody = request.body
        val requestBuffer = okio.Buffer()
        requestBody?.writeTo(requestBuffer)
        val requestBodyString = requestBuffer.readUtf8()
        Log.d("RetrofitRequest", "Request URL: ${request.url}")
        Log.d("RetrofitRequest", "Request Headers: ${request.headers}")
        Log.d("RetrofitRequest", "Request Body: $requestBodyString")

        // Proceed with the request
        val response = chain.proceed(request)

        // Log the response
        val responseBody = response.body
        val responseBodyString = responseBody?.string() ?: "Empty Response"
        Log.d("RetrofitResponse", "Response Code: ${response.code}")
        Log.d("RetrofitResponse", "Response Headers: ${response.headers}")
        Log.d("RetrofitResponse", "Response Body: $responseBodyString")

        // To keep the response body consumable, we need to create a new response with the logged body
        val newResponseBody = responseBodyString.toResponseBody(responseBody?.contentType())
        return response.newBuilder().body(newResponseBody).build()
    }
}