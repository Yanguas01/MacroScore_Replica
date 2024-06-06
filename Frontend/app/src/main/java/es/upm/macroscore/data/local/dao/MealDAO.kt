package es.upm.macroscore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.upm.macroscore.data.local.entities.MealEntity

@Dao
interface MealDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meals: List<MealEntity>)

    @Query("SELECT * FROM meal_table WHERE user_id = :userId ORDER BY `order` ASC")
    suspend fun getMealsByUserId(userId: String): List<MealEntity>
}