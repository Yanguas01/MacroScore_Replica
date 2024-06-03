package es.upm.macroscore.ui.model

import es.upm.macroscore.ui.home.feed.MealState

data class MealUIModel(
    val id: String,
    val name: String,
    val index: Int,
    var items: List<FoodUIModel>,
    var state: MealState
)