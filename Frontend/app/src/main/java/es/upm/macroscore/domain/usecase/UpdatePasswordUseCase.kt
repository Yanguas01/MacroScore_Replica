package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.UserRepository
import es.upm.macroscore.ui.request.UserUpdatePasswordRequest
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(
        updatePasswordRequest: UserUpdatePasswordRequest
    ): Result<Unit> {
        return userRepository.updateMyPassword(updatePasswordRequest)
    }
}