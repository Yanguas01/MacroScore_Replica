package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.MealRepository
import javax.inject.Inject

class DeleteMealUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(mealId: String): Result<Unit> {
        return mealRepository.deleteMeal(mealId)
    }
}