package es.upm.macroscore.domain.repositories

import es.upm.macroscore.domain.model.EmailStatus
import es.upm.macroscore.domain.model.LoginModel
import es.upm.macroscore.domain.model.SignUpModel
import es.upm.macroscore.domain.request.LogInRequest
import es.upm.macroscore.domain.request.SignUpRequest
import es.upm.macroscore.domain.model.UsernameStatus

interface UserRepository {

    suspend fun checkUsername(username: String): Result<UsernameStatus>
    suspend fun checkEmail(email: String): Result<EmailStatus>
    suspend fun registerUser(signUpRequest: SignUpRequest): Result<SignUpModel>
    suspend fun logUser(logInRequest: LogInRequest): Result<LoginModel>
    suspend fun saveMyUser(): Result<Unit>
    suspend fun getUserOrderMeal(): Result<SignUpModel>
}