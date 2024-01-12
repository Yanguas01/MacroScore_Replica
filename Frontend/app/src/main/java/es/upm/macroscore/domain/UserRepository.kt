package es.upm.macroscore.domain

import es.upm.macroscore.data.network.request.signup.SignUpRequest
import es.upm.macroscore.data.network.response.signup.SignUpResponse

interface UserRepository {
    suspend fun registerUser(signUpRequest: SignUpRequest): SignUpResponse?
}