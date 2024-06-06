package es.upm.macroscore.data.network.response.foods

import com.google.gson.annotations.SerializedName

class FoodResponse (
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("kcal_per_100") val kcalPer100: String,
    @SerializedName("carbs_per_100") val carbsPer100: String,
    @SerializedName("prots_per_100") val protsPer100: String,
    @SerializedName("fats_per_100") val fatsPer100: String,
)