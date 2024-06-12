package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke() : Result<Unit> {
        return userRepository.signOut()
    }
}