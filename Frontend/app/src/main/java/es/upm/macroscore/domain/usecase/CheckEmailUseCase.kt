package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.UserRepository
import es.upm.macroscore.domain.model.EmailStatus
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor (private val repository: UserRepository) {
    suspend operator fun invoke(email: String) = repository.checkEmail(email = email)
}