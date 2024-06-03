package es.upm.macroscore.data.network.request.login

import com.google.gson.annotations.SerializedName

data class LogInRequestEntity(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("scope") val scope: String
)