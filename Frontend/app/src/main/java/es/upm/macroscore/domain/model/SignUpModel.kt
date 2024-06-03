package es.upm.macroscore.domain.model

data class SignUpModel (
    val id: String,
    val username: String,
    val email: String,
    val orderMeal: List<String>,
    val profile: UserProfileModel
)