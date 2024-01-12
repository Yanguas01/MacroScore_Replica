package es.upm.macroscore.presentation.auth

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



