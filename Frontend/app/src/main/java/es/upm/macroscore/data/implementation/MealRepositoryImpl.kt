package es.upm.macroscore.data.implementation

import android.util.Log
import es.upm.macroscore.data.mappers.toDTO
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.repositories.MealRepository
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
        Log.d("MealRepository", "addMeal")
        return runCatching {
            Log.d("MealRepository", mealRequest.toDTO().toString())
            val response = macroScoreApiService.addFood(mealRequest.toDTO())
            Log.d("MealRepository", response.body().toString())
            if (response.isSuccessful) {
                Log.d("MealRepository", "response is successful")
                val body = response.body() ?: throw Exception("Empty body")
                body.toDomain()
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }
}