package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.repositories.FoodRepository
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(private val foodRepository: FoodRepository) {

    suspend operator fun invoke(food : FoodModel) {
        foodRepository.addFoodToFavorite(food)
    }
}