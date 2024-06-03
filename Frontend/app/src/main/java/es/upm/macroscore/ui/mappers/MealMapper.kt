package es.upm.macroscore.ui.mappers

import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.model.MealModel
import es.upm.macroscore.ui.home.feed.FoodState
import es.upm.macroscore.ui.home.feed.MealState
import es.upm.macroscore.ui.model.FoodUIModel
import es.upm.macroscore.ui.model.MealUIModel

fun MealModel.toUIModel() = MealUIModel(
    id = this.id,
    name = this.name,
    index = this.index,
    items = this.items.map { item -> item.toUIModel() },
    state = if (this.items.isEmpty()) MealState.EMPTY else MealState.EXPANDED
)

fun FoodModel.toUIModel() = FoodUIModel(
    name = this.name,
    kcalPer100 = this.kcalPer100,
    carbsPer100 = this.carbsPer100,
    protsPer100 = this.protsPer100,
    fatsPer100 = this.fatsPer100,
    state = FoodState.COLLAPSED
)