package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.repositories.MealRepository
import es.upm.macroscore.ui.request.AddFoodRequest
import javax.inject.Inject

class AddFoodToMealUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(
        mealId: String,
        addFoodRequest: AddFoodRequest
    ): Result<FoodModel> {
        return mealRepository.addFoodToMeal(mealId, addFoodRequest)
    }
}