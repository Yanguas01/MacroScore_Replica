package es.upm.macroscore.ui.model

import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.ui.home.feed.FoodState

data class FoodUIModel (
    val id: String,
    val name: String,
    val kcalPer100: Double,
    val carbsPer100: Double,
    val protsPer100: Double,
    val fatsPer100: Double,
    val weight: Double? = null,
    var favorite: Boolean = false,
    var state: FoodState = FoodState.COLLAPSED
) {

    fun toDomain() = FoodModel(
        id = this.id,
        weight = this.weight,
        name = this.name,
        kcalPer100 = this.kcalPer100,
        carbsPer100 = this.carbsPer100,
        protsPer100 = this.protsPer100,
        fatsPer100 = this.fatsPer100
    )
}