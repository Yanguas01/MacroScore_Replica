package es.upm.macroscore.presentation.states

sealed class OnlineValidationState {
    data object Idle: OnlineValidationState()
    data object Loading: OnlineValidationState()
    data class Invalid(val message: String): OnlineValidationState()
    data class Success(val status: Boolean): OnlineValidationState()
    data class Error(val message: String): OnlineValidationState()
}