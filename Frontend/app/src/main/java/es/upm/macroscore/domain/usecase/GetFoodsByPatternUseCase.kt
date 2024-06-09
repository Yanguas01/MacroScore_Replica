package es.upm.macroscore.domain.usecase

import android.util.Log
import androidx.paging.PagingData
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.repositories.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetFoodsByPatternUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {

    suspend operator fun invoke(pattern: String): Flow<PagingData<FoodModel>> {
        return foodRepository.getFoods(pattern)
    }
}