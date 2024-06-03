package es.upm.macroscore.domain.model

data class FoodModel (
    val id: String,
    val weight: Double,
    val name: String,
    val kcalPer100: Double,
    val carbsPer100: Double,
    val protsPer100: Double,
    val fatsPer100: Double
)
