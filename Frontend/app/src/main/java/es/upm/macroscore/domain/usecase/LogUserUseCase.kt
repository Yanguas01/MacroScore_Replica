package es.upm.macroscore.domain.usecase

import es.upm.macroscore.data.network.response.login.LogInResponse
import es.upm.macroscore.domain.model.LoginModel
import es.upm.macroscore.domain.repositories.UserRepository
import es.upm.macroscore.domain.request.LogInRequest
import javax.inject.Inject

class LogUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        username: String, password: String, keepLoggedIn: Boolean
    ): Result<LoginModel> {
        val scope: String = if (keepLoggedIn) "keep_logged_in" else ""
        return userRepository.logUser(LogInRequest(username, password, scope))
    }
}