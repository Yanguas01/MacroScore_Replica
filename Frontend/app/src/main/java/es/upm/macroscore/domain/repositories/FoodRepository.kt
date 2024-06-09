package es.upm.macroscore.domain.repositories

import androidx.paging.PagingData
import es.upm.macroscore.domain.model.FoodModel
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    suspend fun getFoods(pattern: String): Flow<PagingData<FoodModel>>

}