package es.upm.macroscore.ui.states

sealed class OnlineOperationState {
    data object Idle : OnlineOperationState()
    data object Loading : OnlineOperationState()
    data object Success : OnlineOperationState()
    data class Error(val message: String) : OnlineOperationState()
}