package es.upm.macroscore.domain.model

data class MealModel(
    val name: String,
    var foods: List<FoodModel>
)