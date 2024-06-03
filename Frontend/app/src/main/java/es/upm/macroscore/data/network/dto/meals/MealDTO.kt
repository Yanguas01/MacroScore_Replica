package es.upm.macroscore.data.network.dto.meals

import com.google.gson.annotations.SerializedName

data class MealDTO (
    @SerializedName("name") val name: String,
    @SerializedName("datetime") val datetime: String,
    @SerializedName("save_meal") val save_meal: Boolean
)