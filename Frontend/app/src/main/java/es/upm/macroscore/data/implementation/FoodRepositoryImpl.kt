package es.upm.macroscore.data.implementation

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import es.upm.macroscore.data.implementation.paging.FoodPagingSource
import es.upm.macroscore.data.local.dao.FoodDAO
import es.upm.macroscore.data.local.dao.UserFoodDAO
import es.upm.macroscore.data.local.entities.UserFoodEntity
import es.upm.macroscore.data.mappers.toDomain
import es.upm.macroscore.data.mappers.toEntity
import es.upm.macroscore.data.network.MacroScoreApiService
import es.upm.macroscore.data.storage.UserManager
import es.upm.macroscore.domain.model.FoodModel
import es.upm.macroscore.domain.repositories.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FoodRepositoryImpl(
    private val macroScoreApiService: MacroScoreApiService,
    private val foodDAO: FoodDAO,
    private val userFoodDAO: UserFoodDAO,
    private val userManager: UserManager
): FoodRepository {

    override suspend fun getFoods(pattern: String): Flow<PagingData<FoodModel>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {FoodPagingSource(macroScoreApiService, pattern) }
        ).flow
    }

    override suspend fun getFavorites(): Flow<List<FoodModel>> {
        return try {
            userFoodDAO.getFoodsByUserId(userManager.getUserId()).map { list ->
                list.map { food ->
                    food.toDomain()
                }
            }
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error al obtener favoritos: ${e.message}")
            flowOf(emptyList())
        }
    }

    override suspend fun addFoodToFavorite(foodModel: FoodModel) {
        Log.i("FoodRepository", "Inserting Food: ${foodModel.toEntity()} to database")
        foodDAO.insertFood(foodModel.toEntity())
        try {
            Log.i("FoodRepository", "Inserting User-Food: ${UserFoodEntity(userManager.getUserId(), foodModel.id).toString()} to database")
            userFoodDAO.insertUserFoodEntity(UserFoodEntity(userManager.getUserId(), foodModel.id))
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error al añadir favorito: ${e.message}")
        }
    }

    override suspend fun removeFoodFromFavorites(foodId: String) {
        Log.e("FoodRepository", "Deleting Food: ${foodId} from database")
        //userFoodDAO.deleteUserItem(UserFoodEntity(userManager.getUserId(), foodId))
    }
}