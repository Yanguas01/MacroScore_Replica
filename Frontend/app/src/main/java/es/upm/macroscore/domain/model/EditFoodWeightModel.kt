package es.upm.macroscore.domain.model

data class EditFoodWeightModel(
    val mealId: String,
    val foodId: String,
    val weight: Double
)