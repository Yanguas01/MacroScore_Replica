package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.UserRepository
import javax.inject.Inject

class GetMyUserUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke() {
        return
    }
}