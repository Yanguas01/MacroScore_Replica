package es.upm.macroscore.data.network.response.signup

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("order_meal") val orderMeal: List<String>,
    @SerializedName("profile") val profile: UserProfileResponse
)