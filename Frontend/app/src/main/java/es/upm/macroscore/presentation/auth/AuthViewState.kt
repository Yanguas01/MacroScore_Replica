package es.upm.macroscore.presentation.auth

import es.upm.macroscore.domain.model.UsernameStatus

data class AuthViewState(
    var usernameState: UsernameState = UsernameState.Idle,
    var emailState: EmailState = EmailState.Idle,
    var passwordError: String? = null,
    var passwordConfirmedError: String? = null,
    var genderError: String? = null,
    var heightError: String? = null,
    var weightError: String? = null,
    var ageError: String? = null,
    var physicalActivityLevelError: String? = null
) {

    fun isFirstFragmentValid(): Boolean {
        return usernameState == UsernameState.Success(true) &&
                emailState == EmailState.Success(true) &&
                passwordError == null &&
                passwordConfirmedError == null
    }

    fun isValidState(): Boolean {
        return usernameState == UsernameState.Success(true) &&
                emailState == EmailState.Success(true) &&
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
    data object Idle: EmailState()
    data object Loading: EmailState()
    data class Invalid(val message: String): EmailState()
    data class Success(val status: Boolean) : EmailState()
    data class Error(val message: String) : EmailState()
}

sealed class UsernameState {
    data object Idle: UsernameState()
    data object Loading: UsernameState()
    data class Invalid(val message: String): UsernameState()
    data class Success(val status: Boolean) : UsernameState()
    data class Error(val message: String) : UsernameState()
}


