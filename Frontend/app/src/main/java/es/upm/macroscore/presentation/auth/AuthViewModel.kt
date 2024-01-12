package es.upm.macroscore.presentation.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import es.upm.macroscore.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.lang.NumberFormatException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : ViewModel() {

    private var _authViewState = MutableStateFlow(AuthViewState())
    val authViewState: StateFlow<AuthViewState> = _authViewState

    fun isAbleToNav(
        username: String = "",
        email: String = "",
        password: String = "",
        confirmedPassword: String = ""
    ): Boolean {
        validateUsername(username)
        validateEmail(email)
        validatePassword(password)
        validateConfirmedPassword(password, confirmedPassword)

        return _authViewState.value.isFirstFragmentValid()
    }

    fun isAbleToSignUp(
        gender: String = "",
        physicalActivityLevel: String = "",
        height: String = "",
        weight: String = "",
        age: String = ""
    ): Boolean {
        validateGender(gender)
        validatePhysicalActivityLevel(physicalActivityLevel)
        validateHeight(height)
        validateWeight(weight)
        validateAge(age)

        return _authViewState.value.isValidState()
    }

    private fun validateUsername(username: String) {
        _authViewState.update {
            val usernameError: String? = when {
                username.isEmpty() -> "El nombre de usuario no puede estar vacío"
                username.length > 14 -> "El nombre de usuario supera el máximo permitido"
                !username.matches(Regex(("[a-z0-9]+"))) -> "El nombre de usuario solo debe contener letras en minúsculas y dígitos"
                else -> null
            }
            it.copy(usernameError = usernameError)
        }
    }

    private fun validateEmail(email: String) {
        _authViewState.update {
            it.copy(
                emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                ) "Dirección de correo electrónico inválido" else null
            )
        }
    }

    private fun validatePassword(password: String) {
        _authViewState.update {
            it.copy(passwordError = if (password.length < 8) "La contraseña debe tener por lo menos 8 caracteres" else null)
        }
    }

    private fun validateConfirmedPassword(password: String, confirmedPassword: String) {
        _authViewState.update {
            it.copy(passwordConfirmedError = if (password != confirmedPassword) "Las contraseñas no coinciden" else null)
        }
    }

    private fun validateGender(gender: String) {
        _authViewState.update {
            it.copy(genderError = if (
                gender.isEmpty() &&
                gender != context.getString(R.string.male) &&
                gender != context.getString(R.string.female)
                ) "Escoja un género válido" else null
            )
        }
    }

    private fun validatePhysicalActivityLevel(physicalActivityLevel: String) {
        _authViewState.update {
            it.copy(physicalActivityLevelError = if (
                physicalActivityLevel.isEmpty() &&
                physicalActivityLevel != context.getString(R.string.sedentary) &&
                physicalActivityLevel != context.getString(R.string.light_exercise) &&
                physicalActivityLevel != context.getString(R.string.moderate_exercise) &&
                physicalActivityLevel != context.getString(R.string.hard_exercise) &&
                physicalActivityLevel != context.getString(R.string.physical_job)
                ) "Escoja un nivel de actividad válido" else null
            )
        }
    }

    private fun validateHeight(height: String) {
        val heightError: String? = try {
            when (height.toInt()) {
                in 100..300 -> null
                else -> "Introduzca un altura válida"
            }
        } catch (e: NumberFormatException) {
            "Rellene el campo de altura"
        }
        _authViewState.update {
            it.copy(heightError = heightError)
        }
    }

    private fun validateWeight(weight: String) {
        val weightError: String? = try {
            when (weight.toInt()) {
                in 20..400 -> null
                else -> "Introduzca un peso válido"
            }
        } catch (e: NumberFormatException) {
            "Rellene el campo de peso"
        }
        _authViewState.update {
            it.copy(weightError = weightError)
        }
    }

    private fun validateAge(age: String) {
        val ageError: String? = try {
            when (age.toInt()) {
                in 0..150 -> null
                else -> "Introduzca una edad válida"
            }
        } catch (e: NumberFormatException) {
            "Rellene el campo de edad"
        }
        _authViewState.update {
            it.copy(ageError = ageError)
        }
    }
}