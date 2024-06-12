package es.upm.macroscore.data.implementation

import android.util.Log
import es.upm.macroscore.data.local.dao.MealDAO
import es.upm.macroscore.data.local.dao.UserDAO
import es.upm.macroscore.data.local.entities.MealEntity
import es.upm.macroscore.data.mappers.toDTO
import es.upm.macroscore.data.mappers.toDomain
import es.upm.macroscore.data.mappers.toUserEntity
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.storage.TokenManager
import es.upm.macroscore.data.storage.UserManager
import es.upm.macroscore.domain.repositories.UserRepository
import es.upm.macroscore.domain.model.EmailStatus
import es.upm.macroscore.domain.model.LoginModel
import es.upm.macroscore.domain.model.UserModel
import es.upm.macroscore.domain.request.LogInRequest
import es.upm.macroscore.domain.request.SignUpRequest
import es.upm.macroscore.domain.model.UsernameStatus
import es.upm.macroscore.ui.request.UserUpdateRequest
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val macroScoreApiService: MacroScoreApiService,
    private val userDAO: UserDAO,
    private val mealDAO: MealDAO,
    private val tokenManager: TokenManager,
    private val userManager: UserManager
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
    ): Result<UserModel> {
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
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            tokenManager.clearTokens()
        }
    }

    override suspend fun saveMyUser(): Result<Unit> {
        return runCatching {
            val response = macroScoreApiService.getMyUser()

            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                userDAO.insertUser(body.toUserEntity())
                mealDAO.insertAll(body.orderMeal.mapIndexed { i, it ->  MealEntity(userId = body.id, order = i, name = it) })
                userManager.saveUserId(body.id)
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun getUserOrderMeal(): Result<List<String>> {
        return runCatching {
            val userId = userManager.getUserId()

            if (userId.isNotEmpty()) {
                mealDAO.getMealsByUserId(userId).map { mealEntity -> mealEntity.name }
            } else {
                throw Exception("SharedPreferencesError: There is not a user id saved")
            }
        }
    }

    override suspend fun getMyUser(): Result<UserModel> {
        return runCatching {
            val userId = userManager.getUserId()

            if (userId.isNotEmpty()) {
                val orderMeal = mealDAO.getMealsByUserId(userId).map { mealEntity -> mealEntity.name }
                userDAO.getUserById(userId).toDomain(orderMeal)
            } else {
                throw Exception("SharedPreferencesError: There is not a user id saved")
            }
        }
    }

    override suspend fun updateMyUser(userUpdateRequest: UserUpdateRequest): Result<Unit> {
        return runCatching {
            val response = macroScoreApiService.editMyUser(userUpdateRequest.toDTO())

            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                userDAO.insertUser(body.user.toUserEntity())
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }
}