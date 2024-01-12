package es.upm.macroscore.data

import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.network.request.signup.SignUpRequest
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.domain.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    val macroScoreApiService: MacroScoreApiService
): UserRepository {

    override suspend fun registerUser(signUpRequest: SignUpRequest): SignUpResponse? {
        runCatching {
            macroScoreApiService.createNewUser(signUpRequest)
        }
            .onSuccess { }
            .onFailure {  }
        return null
    }
}