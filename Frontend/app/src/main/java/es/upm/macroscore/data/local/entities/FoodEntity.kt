package es.upm.macroscore.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
data class FoodEntity (
    @PrimaryKey
    @ColumnInfo("id") val id: String,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("kcal_per_100") val kcalPer100: Double,
    @ColumnInfo("carbs_per_100") val carbsPer100: Double,
    @ColumnInfo("prots_per_100") val protsPer100: Double,
    @ColumnInfo("fats_per_100") val fatsPer100: Double,
)