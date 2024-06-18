package es.upm.macroscore.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.NO_ACTION

@Entity(
    tableName = "user_food_table",
    primaryKeys = ["user_id", "food_id"],
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = NO_ACTION),
        ForeignKey(entity = FoodEntity::class, parentColumns = ["id"], childColumns = ["food_id"], onDelete = NO_ACTION)
    ]
)
class UserFoodEntity (
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "food_id") val foodId: String
)