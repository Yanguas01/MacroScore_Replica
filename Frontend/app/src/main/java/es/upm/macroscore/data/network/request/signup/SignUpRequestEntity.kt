package es.upm.macroscore.data.network.request.signup

import com.google.gson.annotations.SerializedName

data class SignUpRequestEntity(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("profile") val profile: UserProfileRequestEntity
)