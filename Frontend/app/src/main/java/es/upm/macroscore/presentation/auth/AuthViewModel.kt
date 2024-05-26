package es.upm.macroscore.presentation.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import es.upm.macroscore.R
import es.upm.macroscore.domain.usecase.CheckEmailUseCase
import es.upm.macroscore.domain.usecase.CheckUsernameUseCase
import es.upm.macroscore.domain.usecase.RegisterUserUseCase
import es.upm.macroscore.presentation.states.NoValidationState
import es.upm.macroscore.presentation.states.OnlineValidationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AuthViewModel @Inject constructor(
    val checkUsernameUseCase: CheckUsernameUseCase,
    val checkEmailUseCase: CheckEmailUseCase,
    val registerUserUseCase: RegisterUserUseCase,
    @ApplicationContext val context: Context
) : ViewModel() {

    private var _authViewState = MutableStateFlow(AuthViewState())
    val authViewState: StateFlow<AuthViewState> = _authViewState

    private var usernameJob: Job? = null
    private var emailJob: Job? = null

    fun isAbleToNav(): Boolean {
        return _authViewState.value.isFirstFragmentValid()
    }

    fun isAbleToSignUp(): Boolean {
        return _authViewState.value.isValidState()
    }

    fun validateUsername(username: String) {
        usernameJob?.cancel()
        val trimmedUsername = username.trimEnd()

        val newState = when {
            trimmedUsername.isEmpty() -> OnlineValidationState.Invalid("El nombre de usuario no puede estar vacío")
            trimmedUsername.length > 14 -> OnlineValidationState.Invalid("El nombre de usuario supera el máximo de carácteres permitido")
            !trimmedUsername.matches(Regex(("[a-z0-9]+"))) -> OnlineValidationState.Invalid("El nombre de usuario solo puede contener letras en minúsculas y dígitos")
            else -> {
                usernameJob = viewModelScope.launch {
                    _authViewState.update { it.copy(usernameState = OnlineValidationState.Loading) }
                    checkUsernameUseCase(trimmedUsername)
                        .onSuccess { status ->
                            Log.d("AuthViewModel", status.isAvailable.toString())
                            _authViewState.update {
                                it.copy(
                                    usernameState = OnlineValidationState.Success(
                                        status.isAvailable
                                    )
                                )
                            }
                        }
                        .onFailure { exception ->
                            when (exception) {
                                is IOException -> {
                                    _authViewState.update {
                                        it.copy(
                                            usernameState = OnlineValidationState.Error(
                                                "Error de red: ${exception.message}"
                                            )
                                        )
                                    }
                                }

                                is HttpException -> {
                                    _authViewState.update {
                                        it.copy(
                                            usernameState = OnlineValidationState.Error(
                                                "Error HTTP: ${exception.message}"
                                            )
                                        )
                                    }
                                }

                                is CancellationException -> {
                                    Log.e("AuthViewModel", "Username job cancelado")
                                }

                                else -> {
                                    _authViewState.update {
                                        it.copy(
                                            usernameState = OnlineValidationState.Error(
                                                exception.message ?: "Unknown Error"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                }
                OnlineValidationState.Loading
            }
        }
        _authViewState.update { it.copy(usernameState = newState) }
    }

    fun validateEmail(email: String) {
        emailJob?.cancel()
        val trimmedEmail = email.trimEnd()

        val newState = when {
            trimmedEmail.isEmpty() -> {
                OnlineValidationState.Invalid("La dirección de correo electrónico no puede estar vacía")
            }

            android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                emailJob = viewModelScope.launch {
                    _authViewState.update { it.copy(emailState = OnlineValidationState.Loading) }
                    checkEmailUseCase(trimmedEmail)
                        .onSuccess { status ->
                            _authViewState.update {
                                it.copy(
                                    emailState = OnlineValidationState.Success(
                                        status.isAvailable
                                    )
                                )
                            }
                        }
                        .onFailure { exception ->
                            when (exception) {
                                is IOException -> {
                                    _authViewState.update {
                                        it.copy(
                                            emailState = OnlineValidationState.Error(
                                                "Error de red: ${exception.message}"
                                            )
                                        )
                                    }
                                }

                                is HttpException -> {
                                    _authViewState.update {
                                        it.copy(
                                            emailState = OnlineValidationState.Error(
                                                "Error HTTP: ${exception.message}"
                                            )
                                        )
                                    }
                                }

                                is CancellationException -> {
                                    Log.e("AuthViewModel", "Username job cancelado")
                                }

                                else -> {
                                    _authViewState.update {
                                        it.copy(
                                            emailState = OnlineValidationState.Error(
                                                exception.message ?: "Unknown Error"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                }
                OnlineValidationState.Loading
            }

            else -> {
                OnlineValidationState.Invalid("La dirección de correo electrónico no es válida")
            }
        }

        _authViewState.update { it.copy(emailState = newState) }
    }

    fun validatePassword(password: String) {
        val newState = if (password.length < 8)
            NoValidationState.Invalid("La contraseña es demasiado corta") else NoValidationState.Valid
        _authViewState.update { it.copy(passwordState = newState) }
    }

    fun validateRepeatedPassword(password: String, repeatedPassword: String) {
        val newState = if (password != repeatedPassword)
            NoValidationState.Invalid("Las contraseñas no coinciden") else NoValidationState.Valid
        _authViewState.update { it.copy(repeatedPasswordState = newState) }
    }

    fun validateGender(gender: String) {
        val newState =
            if (gender !in context.resources.getStringArray(R.array.gender)) NoValidationState.Invalid(
                "El género no es válido"
            ) else NoValidationState.Valid
        _authViewState.update { it.copy(genderState = newState) }
    }

    fun validatePhysicalActivityLevel(physicalActivityLevel: String) {
        val newState =
            if (physicalActivityLevel !in context.resources.getStringArray(R.array.physical_activity_level)) NoValidationState.Invalid(
                "La actividad física no es válida"
            ) else NoValidationState.Valid
        _authViewState.update { it.copy(genderState = newState) }
    }

    fun validateHeight(height: String) {
        val newState = try {
            if (height.toInt() in 50..300) NoValidationState.Valid else
                NoValidationState.Invalid("Introduzca una altura válida")
        } catch (e: NumberFormatException) {
            NoValidationState.Invalid("Introduzca una altura válida")
        }
        _authViewState.update { it.copy(heightState = newState) }
    }

    fun validateWeight(weight: String) {
        val newState = try {
            if (weight.toInt() in 20..400) NoValidationState.Valid else
                NoValidationState.Invalid("Introduzca un peso válido")
        } catch (e: NumberFormatException) {
            NoValidationState.Invalid("Introduzca un peso válido")
        }
        _authViewState.update { it.copy(heightState = newState) }
    }

    fun validateAge(age: String) {
        val newState = try {
            if (age.toInt() in 0..150) NoValidationState.Valid else
                NoValidationState.Invalid("Introduzca una edad válida")
        } catch (e: NumberFormatException) {
            NoValidationState.Invalid("Introduzca una edad válida")
        }
        _authViewState.update { it.copy(ageState = newState) }
    }

    fun signup() {

    }
}