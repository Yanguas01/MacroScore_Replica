package es.upm.macroscore.presentation.auth

import es.upm.macroscore.data.network.response.signup.CheckEmailResponse

data class AuthViewState(
    var usernameError: String? = null,
    var emailError: String? = null,
    var passwordError: String? = null,
    var passwordConfirmedError: String? = null,
    var genderError: String? = null,
    var heightError: String? = null,
    var weightError: String? = null,
    var ageError: String? = null,
    var physicalActivityLevelError: String? = null
) {

    fun isFirstFragmentValid(): Boolean {
        return usernameError == null &&
                emailError == null &&
                passwordError == null &&
                passwordConfirmedError == null
    }

    fun isValidState(): Boolean {
        return usernameError == null &&
                emailError == null &&
                passwordError == null &&
                passwordConfirmedError == null &&
                genderError == null &&
                heightError == null &&
                weightError == null &&
                ageError == null &&
                physicalActivityLevelError == null
    }
}

sealed class EmailState {
    object Idle: EmailState()
    object Loading: EmailState()
    object Invalid: EmailState()
    data class Succes(val response: CheckEmailResponse)
}


