package es.upm.macroscore.domain.repositories

import es.upm.macroscore.data.network.response.foods.FoodResponse
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.model.RenameMealModel
import es.upm.macroscore.ui.request.AddFoodRequest
import es.upm.macroscore.ui.request.OrderedMealRequest
import es.upm.macroscore.ui.request.MealRequest

interface MealRepository {

    suspend fun getMealsByDate(date: String): Result<List<MealModel>>
    suspend fun addMeal(mealRequest: MealRequest): Result<MealModel>
    suspend fun renameMeal(mealId: String, newName: String): Result<RenameMealModel>
    suspend fun deleteMeal(mealId: String): Result<Unit>
    suspend fun reorderMeal(orderedMeals: List<OrderedMealRequest>): Result<Unit>
    suspend fun addFoodToMeal(mealId: String, addFoodRequest: AddFoodRequest): Result<FoodModel>

}