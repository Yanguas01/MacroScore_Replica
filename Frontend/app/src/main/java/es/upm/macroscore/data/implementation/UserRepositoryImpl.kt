package es.upm.macroscore.data.implementation

import android.util.Log
import es.upm.macroscore.data.mappers.toDTO
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.network.response.login.LogInResponse
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.data.storage.TokenManager
import es.upm.macroscore.domain.repositories.UserRepository
import es.upm.macroscore.domain.model.EmailStatus
import es.upm.macroscore.domain.model.LoginModel
import es.upm.macroscore.domain.model.SignUpModel
import es.upm.macroscore.domain.request.LogInRequest
import es.upm.macroscore.domain.request.SignUpRequest
import es.upm.macroscore.domain.model.UsernameStatus
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val macroScoreApiService: MacroScoreApiService,
    private val tokenManager: TokenManager
) : UserRepository {

    override suspend fun checkUsername(username: String): Result<UsernameStatus> {
        return runCatching {
            val response = macroScoreApiService.checkUsername(username = username)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                UsernameStatus(
                    isAvailable = body.status == 1
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
                    isAvailable = body.status == 1
                )
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun registerUser(
        signUpRequest: SignUpRequest
    ): Result<SignUpModel> {
        return runCatching {
            val response = macroScoreApiService.createNewUser(signUpRequest.toDTO())
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                body.toDomain()
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun logUser(logInRequest: LogInRequest): Result<LoginModel> {
        val data = logInRequest.toDTO()
        return runCatching {
            val response = macroScoreApiService.logUser(data.username, data.password, data.scope)

            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                tokenManager.saveTokens(body)
                body.toDomain()
            } else {
                Log.d("UserRepository", response.message().toString())
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }
}