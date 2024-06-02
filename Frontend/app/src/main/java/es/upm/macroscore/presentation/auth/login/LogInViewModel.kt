package es.upm.macroscore.presentation.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.data.storage.TokenManager
import es.upm.macroscore.domain.usecase.LogUserUseCase
import es.upm.macroscore.presentation.states.NoValidationFieldState
import es.upm.macroscore.presentation.states.OnlineOperationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    val tokenManager: TokenManager, // TODO eliminar
    val logUserUseCase: LogUserUseCase
) : ViewModel() {

    private val _logInParamsState = MutableStateFlow(LogInParamsState())
    val logInParamsState: StateFlow<LogInParamsState> = _logInParamsState

    private val _logInActionState: MutableStateFlow<OnlineOperationState> =
        MutableStateFlow(OnlineOperationState.Idle)
    val logInActionState: StateFlow<OnlineOperationState> = _logInActionState

    fun isAbleToSignUp(): Boolean {
        return _logInParamsState.value.isValidState()
    }

    fun validateUsername(username: String) {
        val trimmedUsername = username.trimEnd()
        val newState = when {
            trimmedUsername.isEmpty() -> NoValidationFieldState.Invalid("El nombre de usuario no puede estar vacío")
            trimmedUsername.length > 14 -> NoValidationFieldState.Invalid("El nombre de usuario supera el máximo de carácteres permitido")
            !trimmedUsername.matches(Regex(("[a-z0-9]+"))) -> NoValidationFieldState.Invalid("El nombre de usuario solo puede contener letras en minúsculas y dígitos")
            else -> NoValidationFieldState.Valid
        }
        _logInParamsState.update { it.copy(usernameState = newState) }
    }

    fun validatePassword(password: String) {
        val newState = if (password.length < 8)
            NoValidationFieldState.Invalid("La contraseña es demasiado corta")
        else NoValidationFieldState.Valid
        _logInParamsState.update { it.copy(passwordState = newState) }
    }

    fun logIn(
        username: String,
        password: String,
        keepLoggedIn: Boolean
    ) {
        tokenManager.clearTokens()
        viewModelScope.launch {
            _logInActionState.update {
                OnlineOperationState.Loading
            }
            logUserUseCase(
                username = username,
                password = password,
                keepLoggedIn = keepLoggedIn
            )
                .onSuccess {
                    _logInActionState.update { OnlineOperationState.Success }
                }
                .onFailure {
                    Log.d("LogInViewModel", it.message.toString())
                    _logInActionState.update { OnlineOperationState.Error(it.toString()) }
                }
        }
    }
}
