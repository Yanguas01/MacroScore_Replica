package es.upm.macroscore.domain.model

data class NutritionalNeedsModel (
    val dailyKcal: Double,
    val dailyCarbs: Double,
    val dailyProts: Double,
    val dailyFats: Double
)