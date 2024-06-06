package es.upm.macroscore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import es.upm.macroscore.data.local.dao.FoodDAO
import es.upm.macroscore.data.local.dao.MealDAO
import es.upm.macroscore.data.local.dao.UserDAO
import es.upm.macroscore.data.local.dao.UserFoodDAO
import es.upm.macroscore.data.local.entities.FoodEntity
import es.upm.macroscore.data.local.entities.MealEntity
import es.upm.macroscore.data.local.entities.UserEntity
import es.upm.macroscore.data.local.entities.UserFoodEntity

@Database(
    entities = [UserEntity::class, MealEntity::class, FoodEntity::class, UserFoodEntity::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun mealDAO(): MealDAO
    abstract fun foodDAO(): FoodDAO
    abstract fun userFoodDAO(): UserFoodDAO
}