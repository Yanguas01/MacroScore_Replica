package es.upm.macroscore.data.network.response.mealByDate

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("weight") val weight: Double,
    @SerializedName("kcal_per_100") val kcalPer100: Double,
    @SerializedName("carbs_per_100") val carbsPer100: Double,
    @SerializedName("prots_per_100") val protsPer100: Double,
    @SerializedName("fats_per_100") val fatsPer100:Double
)
