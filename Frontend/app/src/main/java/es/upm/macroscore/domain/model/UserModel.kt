package es.upm.macroscore.domain.model

data class UserModel(
    val id: String,
    val username: String,
    val email: String,
    val genderModel: GenderModel
)
