package es.upm.macroscore.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "user_food_table",
    primaryKeys = ["user_id", "food_id"],
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = FoodEntity::class, parentColumns = ["id"], childColumns = ["food_id"], onDelete = ForeignKey.CASCADE)
    ]
)
class UserFoodEntity (
    @ColumnInfo("user_id") val userId: Int,
    @ColumnInfo("food_id") val foodId: Int
)