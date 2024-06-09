package es.upm.macroscore.ui.model

import es.upm.macroscore.ui.home.feed.FoodState

data class FoodUIModel (
    val id: String,
    val name: String,
    val kcalPer100: Double,
    val carbsPer100: Double,
    val protsPer100: Double,
    val fatsPer100: Double,
    val weight: Double? = null,
    var state: FoodState = FoodState.COLLAPSED
)