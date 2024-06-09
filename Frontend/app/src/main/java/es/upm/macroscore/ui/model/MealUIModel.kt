package es.upm.macroscore.ui.model

import es.upm.macroscore.ui.home.feed.MealState

data class MealUIModel(
    val id: String,
    val name: String,
    var index: Int,
    val items: MutableList<FoodUIModel>,
    var state: MealState
)