package es.upm.macroscore.data.network.request.signup

import com.google.gson.annotations.SerializedName

data class UserProfileRequestEntity(
    @SerializedName("gender") val gender: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("age") val age: Int,
    @SerializedName("physical_activity_level") val physicalActivityLevel: Int
)
