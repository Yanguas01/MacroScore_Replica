package es.upm.macroscore.data.mappers

import es.upm.macroscore.data.network.dto.meals.MealDTO
import es.upm.macroscore.ui.request.MealRequest

fun MealRequest.toDTO() = MealDTO(
    name = this.name,
    datetime = this.datetime,
    save_meal = this.saveMeal
)