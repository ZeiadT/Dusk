package iti.mad.dusk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "current_weather_condition",
    primaryKeys = ["owner_location_id", "position"],
    indices = [
        Index(value = ["owner_location_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = CurrentWeatherCacheEntity::class,
            parentColumns = ["locationID"],
            childColumns = ["owner_location_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),

    ]
)
data class CurrentWeatherConditionEntity(

    @ColumnInfo(name = "owner_location_id")
    val ownerLocationId: String,

    /**
     * Index of this entry within the source JSON "weather" array.
     * 0 = primary condition (the one used for icon + description display).
     */
    @ColumnInfo(name = "position")
    val position: Int,

    @ColumnInfo(name = "condition_id")
    val conditionId: Int?,

    @ColumnInfo(name = "main")
    val main: String?,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "icon")
    val icon: String?
)