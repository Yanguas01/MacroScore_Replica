package es.upm.macroscore.domain.usecase

import es.upm.macroscore.data.MealRepository
import es.upm.macroscore.domain.model.MealModel
import javax.inject.Inject

class SaveMealUseCase @Inject constructor(private val mealRepository: MealRepository) {
    operator fun invoke(mealModel: MealModel) {
        mealRepository.updateList(mealModel)
    }
}