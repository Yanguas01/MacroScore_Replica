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
import es.upm.macroscore.domain.usecase.LogUserUseCase
import es.upm.macroscore.domain.usecase.RegisterUserUseCase
import es.upm.macroscore.presentation.model.SignUpFragmentUIModel
import es.upm.macroscore.presentation.states.NoValidationFieldState
import es.upm.macroscore.presentation.states.OnlineOperationState
import es.upm.macroscore.presentation.states.OnlineValidationFieldState
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
    val logUserUseCase: LogUserUseCase,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _authViewParamsState = MutableStateFlow(AuthViewParamsState())
    val authViewParamsState: StateFlow<AuthViewParamsState> = _authViewParamsState

    private val _signUpActionState: MutableStateFlow<OnlineOperationState> = MutableStateFlow(OnlineOperationState.Idle)
    val signUpActionState: StateFlow<OnlineOperationState> = _signUpActionState

    private val _signUpFragmentUIModel = MutableStateFlow(SignUpFragmentUIModel())

    private var usernameJob: Job? = null
    private var emailJob: Job? = null

    fun isAbleToNav(username: String, email: String, password: String): Boolean {
        val isAbleToNav = _authViewParamsState.value.isFirstFragmentValid()
        if (isAbleToNav) {
            _signUpFragmentUIModel.value.username = username
            _signUpFragmentUIModel.value.email = email
            _signUpFragmentUIModel.value.password = password
        }
        return isAbleToNav
    }

    fun isAbleToSignUp(): Boolean {
        Log.d("AuthViewModel",  _authViewParamsState.value.toString())
        return _authViewParamsState.value.isValidState()
    }

    fun validateUsername(username: String) {
        usernameJob?.cancel()
        val trimmedUsername = username.trimEnd()

        val newState = when {
            trimmedUsername.isEmpty() -> OnlineValidationFieldState.Invalid("El nombre de usuario no puede estar vacío")
            trimmedUsername.length > 14 -> OnlineValidationFieldState.Invalid("El nombre de usuario supera el máximo de carácteres permitido")
            !trimmedUsername.matches(Regex(("[a-z0-9]+"))) -> OnlineValidationFieldState.Invalid("El nombre de usuario solo puede contener letras en minúsculas y dígitos")
            else -> {
                usernameJob = viewModelScope.launch {
                    _authViewParamsState.update { it.copy(usernameState = OnlineValidationFieldState.Loading) }
                    checkUsernameUseCase(trimmedUsername)
                        .onSuccess { status ->
                            Log.d("AuthViewModel", status.isAvailable.toString())
                            _authViewParamsState.update {
                                it.copy(
                                    usernameState = OnlineValidationFieldState.Success(
                                        status.isAvailable
                                    )
                                )
                            }
                        }
                        .onFailure { exception ->
                            when (exception) {
                                is IOException -> {
                                    _authViewParamsState.update {
                                        it.copy(
                                            usernameState = OnlineValidationFieldState.Error(
                                                "Error de red: ${exception.message}"
                                            )
                                        )
                                    }
                                }

                                is HttpException -> {
                                    _authViewParamsState.update {
                                        it.copy(
                                            usernameState = OnlineValidationFieldState.Error(
                                                "Error HTTP: ${exception.message}"
                                            )
                                        )
                                    }
                                }

                                is CancellationException -> {
                                    Log.e("AuthViewModel", "Username job cancelado")
                                }

                                else -> {
                                    _authViewParamsState.update {
                                        it.copy(
                                            usernameState = OnlineValidationFieldState.Error(
                                                exception.message ?: "Unknown Error"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                }
                OnlineValidationFieldState.Loading
            }
        }
        _authViewParamsState.update { it.copy(usernameState = newState) }
    }

    fun validateEmail(email: String) {
        emailJob?.cancel()
        val trimmedEmail = email.trimEnd()

        val newState = when {
            trimmedEmail.isEmpty() -> {
                OnlineValidationFieldState.Invalid("La dirección de correo electrónico no puede estar vacía")
            }

            android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                emailJob = viewModelScope.launch {
                    _authViewParamsState.update { it.copy(emailState = OnlineValidationFieldState.Loading) }
                    checkEmailUseCase(trimmedEmail)
                        .onSuccess { status ->
                            _authViewParamsState.update {
                                it.copy(
                                    emailState = OnlineValidationFieldState.Success(
                                        status.isAvailable
                                    )
                                )
                            }
                        }
                        .onFailure { exception ->
                            when (exception) {
                                is IOException -> {
                                    _authViewParamsState.update {
                                        it.copy(
                                            emailState = OnlineValidationFieldState.Error(
                                                "Error de red: ${exception.message}"
                                            )
                                        )
                                    }
                                }

                                is HttpException -> {
                                    _authViewParamsState.update {
                                        it.copy(
                                            emailState = OnlineValidationFieldState.Error(
                                                "Error HTTP: ${exception.message}"
                                            )
                                        )
                                    }
                                }

                                is CancellationException -> {
                                    Log.e("AuthViewModel", "Username job cancelado")
                                }

                                else -> {
                                    _authViewParamsState.update {
                                        it.copy(
                                            emailState = OnlineValidationFieldState.Error(
                                                exception.message ?: "Unknown Error"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                }
                OnlineValidationFieldState.Loading
            }

            else -> {
                OnlineValidationFieldState.Invalid("La dirección de correo electrónico no es válida")
            }
        }

        _authViewParamsState.update { it.copy(emailState = newState) }
    }

    fun validatePassword(password: String) {
        val newState = if (password.length < 8)
            NoValidationFieldState.Invalid("La contraseña es demasiado corta") else NoValidationFieldState.Valid
        _authViewParamsState.update { it.copy(passwordState = newState) }
    }

    fun validateRepeatedPassword(password: String, repeatedPassword: String) {
        val newState = if (password != repeatedPassword)
            NoValidationFieldState.Invalid("Las contraseñas no coinciden") else NoValidationFieldState.Valid
        _authViewParamsState.update { it.copy(repeatedPasswordState = newState) }
    }

    fun validateGender(gender: String) {
        val newState =
            if (gender !in context.resources.getStringArray(R.array.gender)) NoValidationFieldState.Invalid(
                "El género no es válido"
            ) else NoValidationFieldState.Valid
        _authViewParamsState.update { it.copy(genderState = newState) }
    }

    fun validatePhysicalActivityLevel(physicalActivityLevel: String) {
        val newState =
            if (physicalActivityLevel !in context.resources.getStringArray(R.array.physical_activity_level)) NoValidationFieldState.Invalid(
                "La actividad física no es válida"
            ) else NoValidationFieldState.Valid
        _authViewParamsState.update { it.copy(physicalActivityLevelState = newState) }
    }

    fun validateHeight(height: String) {
        val newState = try {
            if (height.toInt() in 50..300) NoValidationFieldState.Valid else
                NoValidationFieldState.Invalid("Introduzca una altura válida")
        } catch (e: NumberFormatException) {
            NoValidationFieldState.Invalid("Introduzca una altura válida")
        }
        _authViewParamsState.update { it.copy(heightState = newState) }
    }

    fun validateWeight(weight: String) {
        val newState = try {
            if (weight.toInt() in 20..400) NoValidationFieldState.Valid else
                NoValidationFieldState.Invalid("Introduzca un peso válido")
        } catch (e: NumberFormatException) {
            NoValidationFieldState.Invalid("Introduzca un peso válido")
        }
        _authViewParamsState.update { it.copy(weightState = newState) }
    }

    fun validateAge(age: String) {
        val newState = try {
            if (age.toInt() in 0..150) NoValidationFieldState.Valid else
                NoValidationFieldState.Invalid("Introduzca una edad válida")
        } catch (e: NumberFormatException) {
            NoValidationFieldState.Invalid("Introduzca una edad válida")
        }
        _authViewParamsState.update { it.copy(ageState = newState) }
    }

    fun signup(
        gender: String,
        physicalActivityLevel: String,
        height: String,
        weight: String,
        age: String
    ) {
        val genderIndex = getGenderIndex(gender)
        val physicalActivityLevelIndex = getPhysicalActivityLevelIndex(physicalActivityLevel)
        viewModelScope.launch {
            _signUpActionState.update { OnlineOperationState.Loading }
            registerUserUseCase(
                username = _signUpFragmentUIModel.value.username,
                email = _signUpFragmentUIModel.value.email,
                gender = genderIndex,
                physicalActivityLevel = physicalActivityLevelIndex,
                height = height.toInt(),
                weight = weight.toInt(),
                age = age.toInt(),
                password = _signUpFragmentUIModel.value.password
            )
                .onSuccess {
                    logUserUseCase(
                        username = _signUpFragmentUIModel.value.username,
                        password = _signUpFragmentUIModel.value.password,
                        keepLoggedIn = false
                    )
                        .onSuccess {
                            _signUpActionState.update { OnlineOperationState.Success }
                        }
                        .onFailure {

                        }
                }
                .onFailure {
                    _signUpActionState.update { OnlineOperationState.Error(it.toString()) }
                }
        }
    }

    private fun getGenderIndex(gender: String): Int {
        return context.resources.getStringArray(R.array.gender).indexOf(gender)
    }

    private fun getPhysicalActivityLevelIndex(physicalActivityLevel: String): Int {
        return context.resources.getStringArray(R.array.physical_activity_level)
            .indexOf(physicalActivityLevel)
    }
}