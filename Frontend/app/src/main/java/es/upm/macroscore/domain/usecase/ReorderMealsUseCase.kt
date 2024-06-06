package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.MealRepository
import es.upm.macroscore.ui.request.OrderedMealRequest
import javax.inject.Inject

class ReorderMealsUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(
        orderedMealList: List<OrderedMealRequest>
    ): Result<Unit> {
        return mealRepository.reorderMeal(orderedMealList)
    }
}