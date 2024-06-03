package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.UserRepository
import javax.inject.Inject

class CheckUsernameUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(username: String) = repository.checkUsername(username = username)
}