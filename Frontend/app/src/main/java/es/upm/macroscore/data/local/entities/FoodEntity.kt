package es.upm.macroscore.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
data class FoodEntity (
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "kcal_per_100") val kcalPer100: Double,
    @ColumnInfo(name = "carbs_per_100") val carbsPer100: Double,
    @ColumnInfo(name = "prots_per_100") val protsPer100: Double,
    @ColumnInfo(name = "fats_per_100") val fatsPer100: Double,
)