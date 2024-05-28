package es.upm.macroscore.domain.usecase

import es.upm.macroscore.data.network.response.login.LogInResponse
import es.upm.macroscore.domain.UserRepository
import es.upm.macroscore.domain.model.LogInRequest
import javax.inject.Inject

class LogUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        username: String, password: String, keepLoggedIn: Boolean
    ): Result<LogInResponse> {
        val scope: List<String> = if (keepLoggedIn) listOf("keep_logged_in") else emptyList()
        return userRepository.logUser(LogInRequest(username, password, scope))
    }
}