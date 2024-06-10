package es.upm.macroscore.domain.model

data class MealModel(
    val id: String,
    val date: String,
    val name: String,
    val index: Int,
    var items: List<FoodModel>
)