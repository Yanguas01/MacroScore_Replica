package es.upm.macroscore.data.network.response.meals

import com.google.gson.annotations.SerializedName
import es.upm.macroscore.domain.model.RenameMealModel

data class RenameMealResponse(
    @SerializedName("message") val message: String,
    @SerializedName("meal_id") val mealId: String,
    @SerializedName("new_name") val newName: String,
) {

    fun toDomain() = RenameMealModel(
        mealId = this.mealId,
        newName = this.newName
    )
}