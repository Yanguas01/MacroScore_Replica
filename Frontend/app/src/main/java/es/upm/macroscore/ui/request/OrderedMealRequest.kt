package es.upm.macroscore.ui.request

data class OrderedMealRequest(
    val id: String,
    val name: String,
    val index: Int
)