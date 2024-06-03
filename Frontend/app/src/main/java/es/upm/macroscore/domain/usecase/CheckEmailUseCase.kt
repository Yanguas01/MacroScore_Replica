package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.UserRepository
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor (private val repository: UserRepository) {
    suspend operator fun invoke(email: String) = repository.checkEmail(email = email)
}