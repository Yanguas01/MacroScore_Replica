package es.upm.macroscore.domain.model

data class ProfileFieldModel (
    val gender: GenderModel,
    val height: Int,
    val weight: Int,
    val age: Int,
    val physicalActivityLevel: PhysicalActivityLevelModel
)
