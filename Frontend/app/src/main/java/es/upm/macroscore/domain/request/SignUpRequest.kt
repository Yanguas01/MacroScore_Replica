package es.upm.macroscore.domain.model

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
    val profile: UserProfileRequest
)


