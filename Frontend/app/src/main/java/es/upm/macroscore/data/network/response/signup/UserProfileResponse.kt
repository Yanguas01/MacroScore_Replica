package es.upm.macroscore.data.network.response.signup

import com.google.gson.annotations.SerializedName
import es.upm.macroscore.domain.model.UserProfileModel

data class UserProfileResponse(
    @SerializedName("gender") val gender: String,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("age") val age: Int,
    @SerializedName("physical_activity_level") val physicalActivityLevel: Int
) {

    fun toDomain() = UserProfileModel(
        gender = this.gender,
        height = this.height,
        weight = this.weight,
        age = this.age,
        physicalActivityLevel = this.physicalActivityLevel
    )
}