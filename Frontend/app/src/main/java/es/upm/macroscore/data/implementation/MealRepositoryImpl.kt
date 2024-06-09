package es.upm.macroscore.data.implementation

import android.util.Log
import es.upm.macroscore.data.mappers.toDTO
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.network.response.meals.EditFoodWeightResponse
import es.upm.macroscore.domain.model.EditFoodWeightModel
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.model.RenameMealModel
import es.upm.macroscore.domain.repositories.MealRepository
import es.upm.macroscore.ui.request.AddFoodRequest
import es.upm.macroscore.ui.request.OrderedMealRequest
import es.upm.macroscore.ui.request.MealRequest
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val macroScoreApiService: MacroScoreApiService
) : MealRepository {

    override suspend fun getMealsByDate(date: String): Result<List<MealModel>> {
        return runCatching {
            val response = macroScoreApiService.getMealsByDate(date)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                body.map { meal ->
                    meal.toDomain()
                }
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun addMeal(mealRequest: MealRequest): Result<MealModel> {
        return runCatching {
            val response = macroScoreApiService.addMeal(mealRequest.toDTO())
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                body.toDomain()
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun renameMeal(mealId: String, newName: String): Result<RenameMealModel> {
        return runCatching {
            val response = macroScoreApiService.renameMeal(mealId = mealId, newMealName = newName)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                body.toDomain()
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun deleteMeal(mealId: String): Result<Unit> {
        return runCatching {
            val response = macroScoreApiService.deleteMeal(mealId = mealId)
            if (!response.isSuccessful) {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun reorderMeal(orderedMeals: List<OrderedMealRequest>): Result<Unit> {
        return runCatching {
            val response = macroScoreApiService.reorderMeal(orderedMeals.map { orderedMeal -> orderedMeal.toDTO() })
            if (!response.isSuccessful) {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun addFoodToMeal(
        mealId: String,
        addFoodRequest: AddFoodRequest
    ): Result<FoodModel> {
        return runCatching {
            val response = macroScoreApiService.addFoodToMeal(mealId, addFoodRequest.toDTO())
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                body.toDomain()
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun editFoodWeight(
        mealId: String,
        foodId: String,
        newWeight: Double
    ): Result<EditFoodWeightModel> {
        return runCatching {
            val response = macroScoreApiService.editFoodWeight(mealId, foodId, newWeight)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                body.toDomain()
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun deleteFood(mealId: String, foodId: String): Result<Unit> {
        return runCatching {
            val response = macroScoreApiService.deleteFood(mealId = mealId, foodId = foodId)
            if (!response.isSuccessful) {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }
}