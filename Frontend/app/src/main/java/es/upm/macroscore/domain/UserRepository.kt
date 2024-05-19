package es.upm.macroscore.domain

import es.upm.macroscore.data.network.request.signup.SignUpRequest
import es.upm.macroscore.data.network.response.signup.CheckEmailResponse
import es.upm.macroscore.data.network.response.signup.SignUpResponse

interface UserRepository {

    suspend fun checkUsername(username: String): Result<Boolean>?
    suspend fun checkEmail(email: String): Result<CheckEmailResponse?>
    suspend fun registerUser(signUpRequest: SignUpRequest): SignUpResponse?
}