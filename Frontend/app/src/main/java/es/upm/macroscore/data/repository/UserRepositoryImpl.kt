package es.upm.macroscore.data.repository

import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.network.response.login.LogInResponse
import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.data.storage.TokenManager
import es.upm.macroscore.domain.UserRepository
import es.upm.macroscore.domain.mapper.LogInRequestMapper
import es.upm.macroscore.domain.mapper.SignUpRequestMapper
import es.upm.macroscore.domain.model.EmailStatus
import es.upm.macroscore.domain.model.LogInRequest
import es.upm.macroscore.domain.model.SignUpRequest
import es.upm.macroscore.domain.model.UsernameStatus
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val macroScoreApiService: MacroScoreApiService,
    private val signUpRequestMapper: SignUpRequestMapper,
    private val logInRequestMapper: LogInRequestMapper,
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
    ): Result<SignUpResponse> {
        return runCatching {
            val response = macroScoreApiService.createNewUser(signUpRequestMapper.map(signUpRequest))
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                SignUpResponse(
                    id = body.id,
                    username = body.username,
                    email = body.email,
                    orderMeal = body.orderMeal,
                    profile = body.profile
                )
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun logUser(logInRequest: LogInRequest): Result<LogInResponse> {
        val data = logInRequestMapper.map(logInRequest)
        return runCatching {
            val response = macroScoreApiService.logUser(data.username, data.password, data.scope)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                tokenManager.saveTokens(body)
                LogInResponse(
                    accessToken = body.accessToken,
                    refreshToken = body.refreshToken,
                    tokenType = body.tokenType
                )
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }
}