package es.upm.macroscore.data.network.dto.meals

import com.google.gson.annotations.SerializedName

data class AddFoodDTO (
    @SerializedName("id") val id: String,
    @SerializedName("weight") val weight: Double
)