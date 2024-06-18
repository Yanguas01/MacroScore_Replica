package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.repositories.FoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(private val foodRepository: FoodRepository) {

    suspend operator fun invoke(): Flow<List<FoodModel>> {
        return foodRepository.getFavorites()
    }
}