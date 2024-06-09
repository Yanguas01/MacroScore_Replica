package es.upm.macroscore.data.network.response.meals

import com.google.gson.annotations.SerializedName
import es.upm.macroscore.domain.model.EditFoodWeightModel

data class EditFoodWeightResponse (
    @SerializedName("message") val message: String,
    @SerializedName("meal_id") val mealId: String,
    @SerializedName("food_id") val foodId: String,
    @SerializedName("weight") val weight: Double
) {

    fun toDomain() = EditFoodWeightModel(
        mealId = this.mealId,
        foodId = this.foodId,
        weight = this.weight
    )
}