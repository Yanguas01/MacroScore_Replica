package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.MealRepository
import javax.inject.Inject

class DeleteFoodUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(mealId: String, foodId: String): Result<Unit> {
        return mealRepository.deleteFood(mealId, foodId)
    }
}