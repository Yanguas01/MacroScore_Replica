package es.upm.macroscore.ui.request

data class MealRequest (
    val name: String,
    val datetime: String,
    val saveMeal: Boolean
)