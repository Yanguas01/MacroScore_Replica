package es.upm.macroscore.data.network.response.login

import com.google.gson.annotations.SerializedName

data class LogInResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("token_type") val tokenType: String
)
