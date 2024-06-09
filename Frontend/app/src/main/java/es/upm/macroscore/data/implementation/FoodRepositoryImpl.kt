package es.upm.macroscore.data.implementation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import es.upm.macroscore.data.implementation.paging.FoodPagingSource
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.repositories.FoodRepository
import kotlinx.coroutines.flow.Flow

class FoodRepositoryImpl(private val macroScoreApiService: MacroScoreApiService): FoodRepository {

    override suspend fun getFoods(pattern: String): Flow<PagingData<FoodModel>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {FoodPagingSource(macroScoreApiService, pattern) }
        ).flow
    }
}