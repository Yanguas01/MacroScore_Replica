package es.upm.macroscore.ui.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.domain.model.UserModel
import es.upm.macroscore.domain.usecase.CheckEmailUseCase
import es.upm.macroscore.domain.usecase.CheckUsernameUseCase
import es.upm.macroscore.domain.usecase.EditUserUseCase
import es.upm.macroscore.domain.usecase.GetMyUserUseCase
import es.upm.macroscore.ui.request.UserUpdateRequest
import es.upm.macroscore.ui.states.OnlineOperationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyUserUseCase: GetMyUserUseCase,
    private val checkUsernameUseCase: CheckUsernameUseCase,
    private val checkEmailUseCase: CheckEmailUseCase,
    private val editUserUseCase: EditUserUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user get(): StateFlow<UserModel?> = _user

    private val _profileState = MutableStateFlow<OnlineOperationState>(OnlineOperationState.Loading)
    val profileState get(): StateFlow<OnlineOperationState> = _profileState

    private val _fieldError = MutableStateFlow<String?>(null)
    val fieldError get(): StateFlow<String?> = _fieldError

    private val _stopAnimationEvent = MutableSharedFlow<Unit>(replay = 0)
    val stopAnimationEvent: SharedFlow<Unit> = _stopAnimationEvent

    private val _closeDialogEvent = MutableSharedFlow<Unit>(replay = 0)
    val closeDialogEvent: SharedFlow<Unit> = _closeDialogEvent

    private var job: Job? = null

    init {
        getMyUser()
    }

    private fun getMyUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getMyUserUseCase().onSuccess { user ->
                    _user.update { user }
                }.onFailure { exception ->
                    handleException(exception)
                }
            }
        }
    }

    private fun handleException(exception: Throwable) {
        when (exception) {
            is IOException -> _profileState.update { OnlineOperationState.Error("Error de red: ${exception.message}") }
            is HttpException -> _profileState.update { OnlineOperationState.Error("Error HTTP: ${exception.message}") }
            else -> _profileState.update { OnlineOperationState.Error("Unknown Error: ${exception.message}") }
        }
    }

    fun editProfile(userField: UserField, newField: String) {
        viewModelScope.launch {

            val userUpdateRequest = UserUpdateRequest()

            when(userField) {
                UserField.USERNAME -> userUpdateRequest.username = newField
                UserField.EMAIL -> userUpdateRequest.email = newField
                UserField.GENDER -> userUpdateRequest.gender = newField.toInt()
                UserField.PHYSICAL_ACTIVITY_LEVEL -> userUpdateRequest.physicalActivityLevel = newField.toInt()
                UserField.HEIGHT -> userUpdateRequest.height = newField.toInt()
                UserField.WEIGHT -> userUpdateRequest.weight = newField.toInt()
                UserField.AGE -> userUpdateRequest.weight = newField.toInt()
            }

            withContext(Dispatchers.IO) {
                editUserUseCase(
                    userUpdateRequest
                )
                    .onSuccess {
                        getMyUserUseCase()
                            .onSuccess { user ->
                                _user.update { user }
                                _closeDialogEvent.emit(Unit)
                            }
                            .onFailure { exception ->
                                handleException(exception)
                            }
                    }
                    .onFailure { exception ->
                        handleException(exception)
                    }
            }
        }
    }

    fun checkUsername(text: String) {
        job?.cancel()
        val trimmedUsername = text.trimEnd()

        viewModelScope.launch {
            val errorMessage = when {
                trimmedUsername.isEmpty() -> "El nombre de usuario no puede estar vacío"
                trimmedUsername.length > 14 -> "El nombre de usuario supera el máximo de carácteres permitido"
                !trimmedUsername.matches(Regex("[a-z0-9]+")) -> "El nombre de usuario solo puede contener letras en minúsculas y dígitos"
                else -> {
                    withContext(Dispatchers.IO) {
                        try {
                            val status = checkUsernameUseCase(trimmedUsername).getOrThrow()
                            if (!status.isAvailable) {
                                "El nombre de usuario ya está asociado a una cuenta"
                            } else {
                                null
                            }
                        } catch (exception: Exception) {
                            handleException(exception)
                            null
                        } finally {
                            _stopAnimationEvent.emit(Unit)
                        }
                    }
                }
            }
            _fieldError.update { errorMessage }
        }
    }

    fun checkEmail(text: String) {
        job?.cancel()
        val trimmedEmail = text.trimEnd()

        viewModelScope.launch {
            val errorMessage = when {
                trimmedEmail.isEmpty() -> "La dirección de correo electrónico no puede estar vacía"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> "La dirección de correo electrónico no es válida"
                else -> {
                    withContext(Dispatchers.IO) {
                        try {
                            val status = checkEmailUseCase(trimmedEmail).getOrThrow()
                            if (!status.isAvailable) {
                                "La dirección de correo electrónico ya está asociado a una cuenta"
                            } else {
                                null
                            }
                        } catch (exception: Exception) {
                            handleException(exception)
                            null
                        } finally {
                            _stopAnimationEvent.emit(Unit)
                        }
                    }
                }
            }
            _fieldError.update { errorMessage }
        }
    }
}