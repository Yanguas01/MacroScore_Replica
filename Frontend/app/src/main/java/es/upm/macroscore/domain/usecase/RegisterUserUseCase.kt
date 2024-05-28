package es.upm.macroscore.domain.usecase

import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.domain.UserRepository
import es.upm.macroscore.domain.model.SignUpRequest
import es.upm.macroscore.domain.model.UserProfileRequest
import javax.inject.Inject
class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        username: String,
        email: String,
        gender: Int,
        physicalActivityLevel: Int,
        height: Int,
        weight: Int,
        age: Int,
        password: String
    ): Result<SignUpResponse> {
        val profile = UserProfileRequest(gender, height, weight, age, physicalActivityLevel)
        return userRepository.registerUser(SignUpRequest(username, email, password, profile))
    }
}

