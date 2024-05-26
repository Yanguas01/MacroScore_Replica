package es.upm.macroscore.domain.usecase

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
        gender: String,
        physicalActivityLevel: Int,
        height: Int,
        weight: Int,
        age: Int,
        password: String
    ) {
        val profile = UserProfileRequest(gender, height, weight, age, physicalActivityLevel)
        userRepository.registerUser(SignUpRequest(username, email, password, profile))
    }
}

