package es.upm.macroscore.domain.model

data class MealsByWeekModel (
    val mealList: List<MealModel>,
    val userProfile: UserProfileModel
)