package es.upm.macroscore.data.network.dto.signup

import com.google.gson.annotations.SerializedName

data class SignUpDTO(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("profile") val profile: UserProfileDTO
)
