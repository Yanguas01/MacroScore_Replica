package es.upm.macroscore.data.network.dto.login

import com.google.gson.annotations.SerializedName

data class LogInDTO(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("scope") val scope: String
)