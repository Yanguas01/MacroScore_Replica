package es.upm.macroscore.ui.request

data class UserUpdateRequest(
    var username: String? = null,
    var email: String? = null,
    var gender: Int? = null,
    var height: Int? = null,
    var weight: Int? = null,
    var age: Int? = null,
    var physicalActivityLevel: Int? = null
)
