package es.upm.macroscore.data.network.response.foods

import com.google.gson.annotations.SerializedName

data class FoodListResponse (
    @SerializedName("results") val foodList: List<FoodResponse>,
    @SerializedName("pagination") val pagination: PaginationResponse
)