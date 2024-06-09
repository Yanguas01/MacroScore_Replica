package es.upm.macroscore.data.network.response.meals

import com.google.gson.annotations.SerializedName
import es.upm.macroscore.domain.model.FoodModel

data class AddFoodResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("kcal_per_100") val kcalPer100: Double,
    @SerializedName("carbs_per_100") val carbsPer100: Double,
    @SerializedName("prots_per_100") val protsPer100: Double,
    @SerializedName("fats_per_100") val fatsPer100: Double,
    @SerializedName("weight") var weight: Double
) {

    fun toDomain() = FoodModel(
        id = this.id,
        name = this.name,
        kcalPer100 = this.kcalPer100,
        carbsPer100 = this.carbsPer100,
        protsPer100 = this.protsPer100,
        fatsPer100 = this.fatsPer100,
        weight = this.weight
    )
}
