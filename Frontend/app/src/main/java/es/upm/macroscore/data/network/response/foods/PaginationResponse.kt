package es.upm.macroscore.data.network.response.foods

import com.google.gson.annotations.SerializedName

data class PaginationResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("skip") val skip: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("next_url") val nextUrl: Int,
)
