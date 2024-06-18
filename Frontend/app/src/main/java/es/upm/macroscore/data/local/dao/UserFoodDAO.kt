package es.upm.macroscore.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import es.upm.macroscore.data.local.entities.FoodEntity
import es.upm.macroscore.data.local.entities.UserFoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFoodDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserFoodEntity(userFoodEntity: UserFoodEntity)

    @Query("SELECT * FROM food_table INNER JOIN user_food_table ON food_table.id = user_food_table.food_id WHERE user_food_table.user_id = :userId")
    fun getFoodsByUserId(userId: String): Flow<List<FoodEntity>>

    @Delete
    suspend fun deleteUserItem(userFoodEntity: UserFoodEntity)
}