package es.upm.macroscore.ui.states

sealed class NoValidationFieldState {
    data object Idle: NoValidationFieldState()
    data class Invalid(val message: String): NoValidationFieldState()
    data object Valid: NoValidationFieldState()
}