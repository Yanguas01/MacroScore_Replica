package es.upm.macroscore.domain.usecase

import es.upm.macroscore.data.repository.FoodRepository
import es.upm.macroscore.domain.model.FoodModel
import javax.inject.Inject

class GetFoodsUseCase @Inject constructor() {
    operator fun invoke(): List<FoodModel> {
        return FoodRepository().getAllFoods()
    }
}