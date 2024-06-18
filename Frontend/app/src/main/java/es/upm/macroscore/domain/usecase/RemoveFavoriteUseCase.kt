package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.repositories.FoodRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(private val foodRepository: FoodRepository) {

    suspend operator fun invoke(foodId : String) {
        foodRepository.removeFoodFromFavorites(foodId)
    }
}