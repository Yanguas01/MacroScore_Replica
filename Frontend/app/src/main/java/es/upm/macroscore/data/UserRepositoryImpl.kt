package es.upm.macroscore.data

import android.util.Log
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.network.request.signup.SignUpRequest
import es.upm.macroscore.data.network.response.signup.CheckEmailResponse
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.domain.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val macroScoreApiService: MacroScoreApiService
): UserRepository {

    override suspend fun checkUsername(username: String): Result<Boolean>? {
        return null
    }

    override suspend fun checkEmail(email: String): Result<CheckEmailResponse?> {
        return runCatching {
            macroScoreApiService.checkEmail(email = email).body()
        }
    }

    override suspend fun registerUser(signUpRequest: SignUpRequest): SignUpResponse? {
        runCatching {
            macroScoreApiService.createNewUser(signUpRequest)
        }
            .onSuccess {

            }
            .onFailure {  }
        return null
    }
}