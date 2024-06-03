package es.upm.macroscore.domain.request

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
    val profile: UserProfileRequest
)


