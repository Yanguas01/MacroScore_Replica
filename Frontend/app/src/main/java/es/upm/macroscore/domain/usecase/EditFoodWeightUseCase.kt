package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.model.EditFoodWeightModel
import es.upm.macroscore.domain.repositories.MealRepository
import javax.inject.Inject

class EditFoodWeightUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(mealId: String, foodId: String, newWeight: Double): Result<EditFoodWeightModel> {
        return mealRepository.editFoodWeight(mealId, foodId, newWeight)
    }
}