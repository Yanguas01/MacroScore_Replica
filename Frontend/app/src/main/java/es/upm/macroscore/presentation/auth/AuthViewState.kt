package es.upm.macroscore.presentation.auth

import es.upm.macroscore.presentation.states.NoValidationState
import es.upm.macroscore.presentation.states.OnlineValidationState

data class AuthViewState(
    var usernameState: OnlineValidationState = OnlineValidationState.Idle,
    var emailState: OnlineValidationState = OnlineValidationState.Idle,
    var passwordState: NoValidationState = NoValidationState.Idle,
    var repeatedPasswordState: NoValidationState = NoValidationState.Idle,
    var genderState: NoValidationState = NoValidationState.Idle,
    var heightState: NoValidationState = NoValidationState.Idle,
    var weightState: NoValidationState = NoValidationState.Idle,
    var ageState: NoValidationState = NoValidationState.Idle,
    var physicalActivityLevelState: NoValidationState = NoValidationState.Idle
) {

    fun isFirstFragmentValid(): Boolean {
        return usernameState == OnlineValidationState.Success(true) &&
                emailState == OnlineValidationState.Success(true) &&
                passwordState == NoValidationState.Valid &&
                repeatedPasswordState == NoValidationState.Valid
    }

    fun isValidState(): Boolean {
        return usernameState == OnlineValidationState.Success(true) &&
                emailState == OnlineValidationState.Success(true) &&
                passwordState == NoValidationState.Valid &&
                repeatedPasswordState == NoValidationState.Valid &&
                genderState == NoValidationState.Valid &&
                heightState == NoValidationState.Valid &&
                weightState == NoValidationState.Valid &&
                ageState == NoValidationState.Valid &&
                physicalActivityLevelState == NoValidationState.Valid
    }
}
