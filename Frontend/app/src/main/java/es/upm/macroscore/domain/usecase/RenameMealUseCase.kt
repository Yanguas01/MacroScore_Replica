package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.model.RenameMealModel
import es.upm.macroscore.domain.repositories.MealRepository
import javax.inject.Inject

class RenameMealUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(mealId: String, newName: String): Result<RenameMealModel> {
        return mealRepository.renameMeal(mealId, newName)
    }
}