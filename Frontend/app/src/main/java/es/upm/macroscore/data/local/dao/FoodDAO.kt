package es.upm.macroscore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.upm.macroscore.data.local.entities.FoodEntity

@Dao
interface FoodDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)

    @Query("SELECT * FROM food_table WHERE id = :foodId")
    suspend fun getFoodById(foodId: String): FoodEntity
}