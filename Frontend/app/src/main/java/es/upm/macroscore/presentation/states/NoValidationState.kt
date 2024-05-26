package es.upm.macroscore.presentation.states

sealed class NoValidationState {
    data object Idle: NoValidationState()
    data class Invalid(val message: String): NoValidationState()
    data object Valid: NoValidationState()
}