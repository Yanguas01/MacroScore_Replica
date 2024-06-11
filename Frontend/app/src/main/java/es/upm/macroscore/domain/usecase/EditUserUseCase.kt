package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.UserRepository
import es.upm.macroscore.ui.request.UserUpdateRequest
import javax.inject.Inject

class EditUserUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(userUpdateRequest: UserUpdateRequest): Result<Unit> {
        return userRepository.updateMyUser(userUpdateRequest)
    }
}