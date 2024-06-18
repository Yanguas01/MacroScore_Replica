package es.upm.macroscore.data.mappers

import es.upm.macroscore.data.local.entities.FoodEntity
import es.upm.macroscore.domain.model.FoodModel


fun FoodModel.toEntity() = FoodEntity(
    id = this.id,
    name = this.name,
    kcalPer100 = this.kcalPer100,
    carbsPer100 = this.carbsPer100,
    protsPer100 = this.protsPer100,
    fatsPer100 = this.fatsPer100
)

fun FoodEntity.toDomain() = FoodModel(
    id = this.id,
    name = this.name,
    kcalPer100 = this.kcalPer100,
    carbsPer100 = this.carbsPer100,
    protsPer100 = this.protsPer100,
    fatsPer100 = this.fatsPer100
)