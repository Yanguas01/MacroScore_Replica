package es.upm.macroscore.domain.model

data class UserFieldsModel(
    val username: String,
    val email: String,
    val password: String,
    val profile: ProfileFieldModel
)
