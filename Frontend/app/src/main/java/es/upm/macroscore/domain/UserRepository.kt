package es.upm.macroscore.domain

import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.domain.model.EmailStatus
import es.upm.macroscore.domain.model.SignUpRequest
import es.upm.macroscore.domain.model.UsernameStatus

interface UserRepository {

    suspend fun checkUsername(username: String): Result<UsernameStatus>
    suspend fun checkEmail(email: String): Result<EmailStatus>
    suspend fun registerUser(signUpRequest: SignUpRequest): Result<SignUpResponse>
}