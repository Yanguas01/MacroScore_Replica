package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.UserRepository
import javax.inject.Inject

class DeleteMealFromSavedMealsUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(
        mealName: String
    ): Result<Unit> {
        return userRepository.deleteMealFromSavedMeals(mealName)
    }
}