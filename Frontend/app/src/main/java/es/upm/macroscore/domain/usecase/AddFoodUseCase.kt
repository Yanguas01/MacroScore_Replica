package es.upm.macroscore.domain.usecase

import es.upm.macroscore.data.MealRepository
import es.upm.macroscore.domain.model.FoodModel
import javax.inject.Inject

class AddFoodUseCase @Inject constructor(private val mealRepository: MealRepository) {
    operator fun invoke(foodModel: FoodModel, mealName: String) {
        mealRepository.updateMeal(foodModel, mealName)
    }
}