package es.upm.macroscore.data.network.dto.meals

import com.google.gson.annotations.SerializedName

data class OrderedMealDTO(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("index") val index: Int
)
