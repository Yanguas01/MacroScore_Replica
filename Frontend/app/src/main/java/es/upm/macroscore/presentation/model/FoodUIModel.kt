package es.upm.macroscore.presentation.model

import es.upm.macroscore.presentation.home.feed.FoodState

data class FoodUIModel (
    val name: String,
    val kcalPer100: Double,
    val carbsPer100: Double,
    val protsPer100: Double,
    val fatsPer100: Double,
    var state: FoodState = FoodState.COLLAPSED
)