package es.upm.macroscore.ui.mappers

import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.ui.home.feed.MealState
import es.upm.macroscore.ui.model.MealUIModel

fun MealModel.toUIModel() = MealUIModel(
    id = this.id,
    name = this.name,
    index = this.index,
    items = this.items.map { item -> item.toUIModel() }.toMutableList(),
    state = if (this.items.isEmpty()) MealState.EMPTY else MealState.EXPANDED
)
