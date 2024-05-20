package es.upm.macroscore.data

import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.network.request.signup.SignUpRequest
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.domain.UserRepository
import es.upm.macroscore.domain.model.EmailStatus
import es.upm.macroscore.domain.model.UsernameStatus
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val macroScoreApiService: MacroScoreApiService
) : UserRepository {

    override suspend fun checkUsername(username: String): Result<UsernameStatus> {
        return runCatching {
            val response = macroScoreApiService.checkUsername(username = username)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                UsernameStatus(
                    isAvailable = body.status == 0
                )
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun checkEmail(email: String): Result<EmailStatus> {
        return runCatching {
            val response = macroScoreApiService.checkEmail(email = email)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                EmailStatus(
                    isAvailable = body.status == 0
                )
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun registerUser(signUpRequest: SignUpRequest): SignUpResponse? {
        runCatching {
            macroScoreApiService.createNewUser(signUpRequest)
        }
            .onSuccess {

            }
            .onFailure { }
        return null
    }
}