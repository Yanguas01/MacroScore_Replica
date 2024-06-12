package es.upm.macroscore.ui.request

data class UserUpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
