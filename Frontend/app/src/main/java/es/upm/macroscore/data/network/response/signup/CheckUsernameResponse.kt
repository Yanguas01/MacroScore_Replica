package es.upm.macroscore.data.network.response.signup

import com.google.gson.annotations.SerializedName

data class CheckUsernameResponse (
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Int,
    @SerializedName("username") val username: String
)