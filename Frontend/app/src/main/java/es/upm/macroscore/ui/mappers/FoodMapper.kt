package es.upm.macroscore.ui.mappers

import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.ui.model.FoodUIModel

fun FoodModel.toUIModel() = FoodUIModel(
    id = this.id,
    weight = this.weight,
    name = this.name,
    kcalPer100 = this.kcalPer100,
    carbsPer100 = this.carbsPer100,
    protsPer100 = this.protsPer100,
    fatsPer100 = this.fatsPer100
)