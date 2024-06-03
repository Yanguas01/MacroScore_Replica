package es.upm.macroscore.domain.model

data class UserProfileModel (
    val gender: String,
    val height: Int,
    val weight: Int,
    val age: Int,
    val physicalActivityLevel: Int
)