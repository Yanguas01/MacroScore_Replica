package es.upm.macroscore.data.network.dto.users

import com.google.gson.annotations.SerializedName

data class UserUpdateDTO(
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("gender") val gender: Int? = null,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("weight") val weight: Int? = null,
    @SerializedName("age") val age: Int? = null,
    @SerializedName("physical_activity_level") val physicalActivityLevel: Int? = null
)
