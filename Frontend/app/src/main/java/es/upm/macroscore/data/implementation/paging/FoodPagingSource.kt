package es.upm.macroscore.data.implementation.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.domain.model.FoodModel

class FoodPagingSource(
    private val macroScoreApiService: MacroScoreApiService,
    private val pattern: String
) : PagingSource<Int, FoodModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoodModel> {
        return try {
            val skip = params.key ?: 0
            val limit = params.loadSize

            val response = macroScoreApiService.getFoodsByPattern(pattern, skip, limit)

            if (response.isSuccessful) {

                val body = response.body() ?: throw Exception("Empty body")
                if (body.foodList.isEmpty()) throw Exception("No hay resultados de la búsqueda")

                val nextKey = if (body.pagination.nextUrl != null) skip + limit else null
                LoadResult.Page(
                    data = body.foodList.map { foodResponse -> foodResponse.toModel() },
                    prevKey = if (skip == 0) null else skip - limit,
                    nextKey = nextKey
                )
            } else {
                throw Exception("Server error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FoodModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}