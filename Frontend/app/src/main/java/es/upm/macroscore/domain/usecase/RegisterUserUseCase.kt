package es.upm.macroscore.domain.usecase

import es.upm.macroscore.data.network.response.signup.SignUpResponse
import es.upm.macroscore.domain.model.SignUpModel
import es.upm.macroscore.domain.repositories.UserRepository
import es.upm.macroscore.domain.request.SignUpRequest
import es.upm.macroscore.domain.request.UserProfileRequest
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
    ): Result<SignUpModel> {
        val profile = UserProfileRequest(gender, height, weight, age, physicalActivityLevel)
        return userRepository.registerUser(SignUpRequest(username, email, password, profile))
    }
}

