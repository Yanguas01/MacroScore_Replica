package es.upm.macroscore.domain

import es.upm.macroscore.data.network.response.meals.MealByDateResponse

interface MealRepository {

    suspend fun getMealsByDate(date: String): Result<List<MealByDateResponse>>

}