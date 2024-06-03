package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.repositories.MealRepository
import es.upm.macroscore.ui.request.MealRequest
import javax.inject.Inject

class AddMealUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(
        mealRequest: MealRequest
    ): Result<MealModel> {
        return mealRepository.addMeal(mealRequest)
    }
}