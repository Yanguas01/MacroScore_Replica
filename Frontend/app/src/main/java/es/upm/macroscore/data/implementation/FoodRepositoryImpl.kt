package es.upm.macroscore.data.implementation

import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.domain.repositories.FoodRepository

class FoodRepositoryImpl(private val macroScoreApiService: MacroScoreApiService): FoodRepository {

    override suspend fun getFoods(pattern: String, skip: Int, limit: Int) {
        val response = macroScoreApiService.getFoodsByPattern(pattern, skip, limit)
    }
}