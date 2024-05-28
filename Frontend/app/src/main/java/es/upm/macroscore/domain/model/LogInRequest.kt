package es.upm.macroscore.domain.model

data class LogInRequest (
    val username: String,
    val password: String,
    val scope: List<String>
)
