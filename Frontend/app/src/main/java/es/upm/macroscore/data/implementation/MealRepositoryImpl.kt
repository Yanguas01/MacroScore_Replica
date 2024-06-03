package es.upm.macroscore.data.repository

import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.network.response.meals.MealByDateResponse
import es.upm.macroscore.data.network.response.meals.MealItemResponse
import es.upm.macroscore.domain.MealRepository
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val macroScoreApiService: MacroScoreApiService
) : MealRepository {

    override suspend fun getMealsByDate(date: String): Result<List<MealByDateResponse>> {
        return runCatching {
            val response = macroScoreApiService.getMealsByDate(date)
            if (response.isSuccessful) {
                val body = response.body() ?: throw Exception("Empty body")
                body.map { meal ->
                    MealByDateResponse(
                        id = meal.id,
                        userId = meal.userId,
                        date = meal.date,
                        name = meal.name,
                        index = meal.index,
                        mealItemResponses = meal.mealItemResponses.map { item ->
                            MealItemResponse(
                                id = item.id,
                                weight = item.weight,
                                name = item.name,
                                kcalPer100 = item.kcalPer100,
                                carbsPer100 = item.carbsPer100,
                                protsPer100 = item.protsPer100,
                                fatsPer100 = item.fatsPer100
                            )
                        }
                    )
                }
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        }
    }
}