package es.upm.macroscore.data.network.response.meals

import com.google.gson.annotations.SerializedName
import es.upm.macroscore.domain.model.MealModel

data class MealByDateResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("date") val date: String,
    @SerializedName("name") val name: String,
    @SerializedName("index") val index: Int,
    @SerializedName("items") val mealItemResponses: List<MealItemResponse>
) {

    fun toDomain() = MealModel(
        id = this.id,
        name = this.name,
        date = this.date,
        index = this.index,
        items = this.mealItemResponses.map { item -> item.toDomain() }
    )
}
