package es.upm.macroscore.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_table",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["user_id", "name"], unique = true)]
)
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = -1,
    @ColumnInfo("user_id") val userId: String,
    @ColumnInfo("order") val order: Int,
    @ColumnInfo("name") val name: String,
)
