package es.upm.macroscore.domain.request

data class LogInRequest (
    val username: String,
    val password: String,
    val scope: String
)
