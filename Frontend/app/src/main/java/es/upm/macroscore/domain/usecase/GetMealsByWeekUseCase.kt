package es.upm.macroscore.domain.usecase

import es.upm.macroscore.domain.model.DailyIntakeModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.domain.model.NutritionalNeedsModel
import es.upm.macroscore.domain.model.UserProfileModel
import es.upm.macroscore.domain.repositories.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMealsByWeekUseCase @Inject constructor(private val mealRepository: MealRepository) {

    suspend operator fun invoke(startDateWeek: String, endDateWeek: String): Result<Pair<NutritionalNeedsModel, Map<String, DailyIntakeModel>>> {
        return withContext(Dispatchers.IO) {
            val result = mealRepository.getMealsByWeek(startDateWeek, endDateWeek)
            if (result.isSuccess) {
                val body = result.getOrNull()!!
                val dailyIntakeMap = calculateDailyIntake(body.mealList)
                val nutritionalNeeds = calculateNutritionalNeeds(body.userProfile)
                Result.success(Pair(nutritionalNeeds, dailyIntakeMap))
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        }
    }

    private fun calculateDailyIntake(meals: List<MealModel>): Map<String, DailyIntakeModel> {
        val dailyIntakeMap = mutableMapOf<String, DailyIntakeModel>()
        meals.forEach { meal ->
            val date = meal.date.substring(0, 10)
            val dailyIntake = dailyIntakeMap[date] ?: DailyIntakeModel(0.0, 0.0, 0.0, 0.0)
            val updatedIntake = meal.items.fold(dailyIntake) { acc, item ->
                val kcal = acc.totalKcal + (item.weight?.times(item.kcalPer100) ?: 0.0) / 100
                val carbs = acc.totalCarbs + (item.weight?.times(item.carbsPer100) ?: 0.0) / 100
                val prots = acc.totalProts + (item.weight?.times(item.protsPer100) ?: 0.0) / 100
                val fats = acc.totalFats + (item.weight?.times(item.fatsPer100) ?: 0.0) / 100
                DailyIntakeModel(kcal, carbs, prots, fats)
            }
            dailyIntakeMap[date] = updatedIntake
        }
        return dailyIntakeMap
    }

    private fun calculateNutritionalNeeds(userProfile: UserProfileModel): NutritionalNeedsModel {
        val tmb = if (userProfile.gender == 0) {
            (10 * userProfile.weight) + (6.25 * userProfile.height) - (5.0 * userProfile.age) + 5
        } else {
            (10 * userProfile.weight) + (6.25 * userProfile.height) - (5.0 * userProfile.age) - 161
        }

        val activityFactor = when (userProfile.physicalActivityLevel) {
            0 -> 1.2
            1 -> 1.375
            2 -> 1.55
            3 -> 1.725
            4 -> 1.9
            else -> 1.0
        }

        val kcalNeeds = tmb * activityFactor

        val carbsNeeds = kcalNeeds * 0.5 / 4
        val protsNeeds = kcalNeeds * 0.2 / 4
        val fatsNeeds = kcalNeeds * 0.3 / 9

        return NutritionalNeedsModel(kcalNeeds, carbsNeeds, protsNeeds, fatsNeeds)
    }
}

