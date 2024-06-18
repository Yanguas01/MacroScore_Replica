package es.upm.macroscore.data.storage

import android.content.SharedPreferences
import android.util.Log
import dagger.Lazy
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.network.response.login.LogInResponse
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val macroScoreApiService: Lazy<MacroScoreApiService>
) {

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    private fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun saveTokens(logInResponse: LogInResponse) {
        Log.e("TokenManager", "Access Token: ${logInResponse.accessToken}")
        Log.e("TokenManager", "Refresh Token: ${logInResponse.refreshToken}")
        sharedPreferences.edit().putString("access_token", logInResponse.accessToken).apply()
        sharedPreferences.edit().putString("refresh_token", logInResponse.refreshToken).apply()
    }

    fun clearTokens() {
        sharedPreferences.edit().remove("access_token").apply()
        sharedPreferences.edit().remove("refresh_token").apply()
    }

    suspend fun refreshAccessToken(): String? {
        Log.e("TokenManager", "Refresh Token: ${getRefreshToken().toString()}")
        val refreshToken = getRefreshToken() ?: return null

        val result = runCatching {
            val response = macroScoreApiService.get().refreshToken(refreshToken = refreshToken)

            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                saveTokens(logInResponse = body)
                body.accessToken
            } else {
                if (response.code() == 401) {
                    clearTokens()
                }
                throw Exception("Token refresh failed with code: ${response.code()}")
            }
        }

        return result.getOrNull()
    }
}