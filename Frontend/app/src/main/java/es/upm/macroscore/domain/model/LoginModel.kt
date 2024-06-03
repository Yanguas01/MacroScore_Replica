package es.upm.macroscore.domain.model

data class LoginModel (
    val accessToken: String,
    val refreshToken: String?,
    val tokenType: String
)