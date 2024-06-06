package es.upm.macroscore.domain.repositories

interface FoodRepository {

    suspend fun getFoods(pattern: String, skip: Int, limit: Int)

}