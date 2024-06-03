package es.upm.macroscore.data.network.dto.signup

import com.google.gson.annotations.SerializedName

data class UserProfileDTO(
    @SerializedName("gender") val gender: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("age") val age: Int,
    @SerializedName("physical_activity_level") val physicalActivityLevel: Int
)
