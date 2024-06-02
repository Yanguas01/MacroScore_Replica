package es.upm.macroscore.presentation.auth.login

import es.upm.macroscore.presentation.states.NoValidationFieldState


data class LogInParamsState(
    var usernameState: NoValidationFieldState = NoValidationFieldState.Idle,
    var passwordState: NoValidationFieldState = NoValidationFieldState.Idle
) {

    fun isValidState(): Boolean {
        return usernameState == NoValidationFieldState.Valid && passwordState == NoValidationFieldState.Valid
    }
}
