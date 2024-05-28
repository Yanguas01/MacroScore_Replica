package es.upm.macroscore.presentation.auth

import es.upm.macroscore.presentation.states.NoValidationFieldState
import es.upm.macroscore.presentation.states.OnlineValidationFieldState

data class AuthViewParamsState(
    var usernameState: OnlineValidationFieldState = OnlineValidationFieldState.Idle,
    var emailState: OnlineValidationFieldState = OnlineValidationFieldState.Idle,
    var passwordState: NoValidationFieldState = NoValidationFieldState.Idle,
    var repeatedPasswordState: NoValidationFieldState = NoValidationFieldState.Idle,
    var genderState: NoValidationFieldState = NoValidationFieldState.Idle,
    var heightState: NoValidationFieldState = NoValidationFieldState.Idle,
    var weightState: NoValidationFieldState = NoValidationFieldState.Idle,
    var ageState: NoValidationFieldState = NoValidationFieldState.Idle,
    var physicalActivityLevelState: NoValidationFieldState = NoValidationFieldState.Idle
) {

    fun isFirstFragmentValid(): Boolean {
        return usernameState == OnlineValidationFieldState.Success(true) &&
                emailState == OnlineValidationFieldState.Success(true) &&
                passwordState == NoValidationFieldState.Valid &&
                repeatedPasswordState == NoValidationFieldState.Valid
    }

    fun isValidState(): Boolean {
        return usernameState == OnlineValidationFieldState.Success(true) &&
                emailState == OnlineValidationFieldState.Success(true) &&
                passwordState == NoValidationFieldState.Valid &&
                repeatedPasswordState == NoValidationFieldState.Valid &&
                genderState == NoValidationFieldState.Valid &&
                heightState == NoValidationFieldState.Valid &&
                weightState == NoValidationFieldState.Valid &&
                ageState == NoValidationFieldState.Valid &&
                physicalActivityLevelState == NoValidationFieldState.Valid
    }
}
