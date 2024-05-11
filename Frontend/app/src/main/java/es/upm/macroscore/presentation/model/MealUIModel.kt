package es.upm.macroscore.presentation.model

import es.upm.macroscore.presentation.home.feed.MealState

data class MealUIModel(
    val name: String,
    var foods: List<FoodUIModel>,
    var state: MealState? = null
)