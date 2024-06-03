package es.upm.macroscore.ui.states

sealed class OnlineValidationFieldState {
    data object Idle: OnlineValidationFieldState()
    data object Loading: OnlineValidationFieldState()
    data class Invalid(val message: String): OnlineValidationFieldState()
    data class Success(val status: Boolean): OnlineValidationFieldState()
    data class Error(val message: String): OnlineValidationFieldState()
}