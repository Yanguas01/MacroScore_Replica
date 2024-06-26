package es.upm.macroscore.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.upm.macroscore.core.exceptions.BadRequestException
import es.upm.macroscore.domain.model.UserModel
import es.upm.macroscore.domain.usecase.CheckEmailUseCase
import es.upm.macroscore.domain.usecase.CheckUsernameUseCase
import es.upm.macroscore.domain.usecase.DeleteMealFromSavedMealsUseCase
import es.upm.macroscore.domain.usecase.EditUserUseCase
import es.upm.macroscore.domain.usecase.GetMyUserUseCase
import es.upm.macroscore.domain.usecase.SignOutUseCase
import es.upm.macroscore.domain.usecase.UpdatePasswordUseCase
import es.upm.macroscore.ui.request.UserUpdatePasswordRequest
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
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyUserUseCase: GetMyUserUseCase,
    private val checkUsernameUseCase: CheckUsernameUseCase,
    private val checkEmailUseCase: CheckEmailUseCase,
    private val editUserUseCase: EditUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteMealFromSavedMealsUseCase: DeleteMealFromSavedMealsUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user get(): StateFlow<UserModel?> = _user

    private val _profileState = MutableStateFlow<OnlineOperationState>(OnlineOperationState.Loading)
    val profileState get(): StateFlow<OnlineOperationState> = _profileState

    private val _fieldError = MutableStateFlow<String?>(null)
    val fieldError get(): StateFlow<String?> = _fieldError

    private val _stopAnimationEvent = MutableSharedFlow<Unit>(replay = 0)
    val stopAnimationEvent get(): SharedFlow<Unit> = _stopAnimationEvent

    private val _closeSheetEvent = MutableSharedFlow<Unit>(replay = 0)
    val closeSheetEvent get(): SharedFlow<Unit> = _closeSheetEvent

    private val _updatePasswordBottomSheetState = MutableStateFlow<OnlineOperationState>(OnlineOperationState.Idle)
    val updatePasswordBottomSheetState get() : StateFlow<OnlineOperationState> = _updatePasswordBottomSheetState

    private val _signOutEvent = MutableSharedFlow<Unit>(replay = 0)
    val signOutEvent get(): SharedFlow<Unit> = _signOutEvent

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
            is CancellationException -> Log.e("ProfileViewModel", "Job Cancelado")
            else -> _profileState.update { OnlineOperationState.Error("Unknown Error: ${exception.message}") }
        }
    }

    fun editProfile(userField: UserField, newField: String) {
        viewModelScope.launch {

            val userUpdateRequest = UserUpdateRequest()

            when (userField) {
                UserField.USERNAME -> userUpdateRequest.username = newField
                UserField.EMAIL -> userUpdateRequest.email = newField
                UserField.GENDER -> userUpdateRequest.gender = newField.toInt()
                UserField.PHYSICAL_ACTIVITY_LEVEL -> userUpdateRequest.physicalActivityLevel =
                    newField.toInt()

                UserField.HEIGHT -> userUpdateRequest.height = newField.toInt()
                UserField.WEIGHT -> userUpdateRequest.weight = newField.toInt()
                UserField.AGE -> userUpdateRequest.age = newField.toInt()
            }

            withContext(Dispatchers.IO) {
                editUserUseCase(
                    userUpdateRequest
                ).onSuccess {
                        if (userField == UserField.USERNAME) {
                            signOutUseCase().onSuccess {
                                    _signOutEvent.emit(Unit)
                                }.onFailure { exception ->
                                    handleException(exception)
                                }
                        }
                        getMyUserUseCase().onSuccess { user ->
                                _user.update { user }
                                _closeSheetEvent.emit(Unit)
                            }.onFailure { exception ->
                                handleException(exception)
                            }
                    }.onFailure { exception ->
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
                !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail)
                    .matches() -> "La dirección de correo electrónico no es válida"

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

    fun signOut() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                signOutUseCase().onSuccess {
                        _signOutEvent.emit(Unit)
                    }.onFailure { exception ->
                        handleException(exception)
                    }
            }
        }
    }

    fun deleteMealFromSavedMeals(mealName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deleteMealFromSavedMealsUseCase(mealName).onSuccess {
                        getMyUserUseCase()
                            .onSuccess { user ->
                                _user.update { user }
                            }.onFailure { exception ->
                                handleException(exception)
                            }
                    }.onFailure { exception ->
                        handleException(exception)
                    }
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String, repeatedPassword: String) {
        if (newPassword.length < 8) {
            _updatePasswordBottomSheetState.update { OnlineOperationState.Error("La contraseña debe superar los 7 caracteres", ProfileErrorCodes.ERROR_BAD_INPUT) }
        } else if (newPassword != repeatedPassword) {
            _updatePasswordBottomSheetState.update { OnlineOperationState.Error("Las contraseñas no coinciden", ProfileErrorCodes.ERROR_PASSWORDS_DONT_MATCHES) }
        } else {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _updatePasswordBottomSheetState.update { OnlineOperationState.Loading }
                updatePasswordUseCase(
                    UserUpdatePasswordRequest(oldPassword, newPassword)
                )
                    .onSuccess {
                        _updatePasswordBottomSheetState.update { OnlineOperationState.Success }
                    }
                    .onFailure { exception ->
                        when (exception) {
                            is IOException -> _updatePasswordBottomSheetState.update {
                                OnlineOperationState.Error(
                                    "Error de red: ${exception.message}"
                                )
                            }

                            is HttpException -> _updatePasswordBottomSheetState.update {
                                OnlineOperationState.Error(
                                    "Error HTTP: ${exception.message}"
                                )
                            }

                            is BadRequestException -> _updatePasswordBottomSheetState.update {
                                OnlineOperationState.Error(
                                    "La contraseña no es correcta", ProfileErrorCodes.ERROR_BAD_REQUEST
                                )
                            }

                            else -> _updatePasswordBottomSheetState.update {
                                OnlineOperationState.Error(
                                    "Unknown Error: ${exception.message}"
                                )
                            }
                        }
                    }
            }
            }
        }
    }
}