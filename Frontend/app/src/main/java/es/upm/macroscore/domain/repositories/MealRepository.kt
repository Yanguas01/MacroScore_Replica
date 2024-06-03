package es.upm.macroscore.domain.repositories

import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.ui.request.MealRequest

interface MealRepository {

    suspend fun getMealsByDate(date: String): Result<List<MealModel>>
    suspend fun addMeal(mealRequest: MealRequest): Result<MealModel>

}