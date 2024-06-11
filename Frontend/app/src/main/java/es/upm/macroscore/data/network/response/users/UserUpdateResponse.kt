package es.upm.macroscore.data.network.response.users

import com.google.gson.annotations.SerializedName
import es.upm.macroscore.data.network.response.signup.SignUpResponse

data class UserUpdateResponse (
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: SignUpResponse
)