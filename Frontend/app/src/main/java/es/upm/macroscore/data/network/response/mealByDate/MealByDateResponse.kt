package es.upm.macroscore.data.network.response.mealByDate

import com.google.gson.annotations.SerializedName

data class MealByDateResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("date") val date: String,
    @SerializedName("name") val name: String,
    @SerializedName("index") val index: Int,
    @SerializedName("items") val items: List<Item>
)
