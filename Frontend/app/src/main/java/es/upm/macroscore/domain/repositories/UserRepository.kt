package es.upm.macroscore.domain.repositories

import es.upm.macroscore.domain.model.EmailStatus
import es.upm.macroscore.domain.model.LoginModel
import es.upm.macroscore.domain.model.UserModel
import es.upm.macroscore.domain.request.LogInRequest
import es.upm.macroscore.domain.request.SignUpRequest
import es.upm.macroscore.domain.model.UsernameStatus
import es.upm.macroscore.ui.request.UserUpdateRequest

interface UserRepository {

    suspend fun checkUsername(username: String): Result<UsernameStatus>
    suspend fun checkEmail(email: String): Result<EmailStatus>
    suspend fun registerUser(signUpRequest: SignUpRequest): Result<UserModel>
    suspend fun logUser(logInRequest: LogInRequest): Result<LoginModel>
    suspend fun signOut(): Result<Unit>
    suspend fun saveMyUser(): Result<Unit>
    suspend fun getUserOrderMeal(): Result<List<String>>
    suspend fun getMyUser(): Result<UserModel>
    suspend fun updateMyUser(userUpdateRequest: UserUpdateRequest): Result<Unit>
}