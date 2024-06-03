package es.upm.macroscore.domain.usecase

import es.upm.macroscore.data.network.response.meals.MealByDateResponse
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.repositories.MealRepository
import javax.inject.Inject

class GetMealsByDateUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(date: String): Result<List<MealModel>> {
        return mealRepository.getMealsByDate(date)
    }

}