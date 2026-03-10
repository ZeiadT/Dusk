package iti.mad.dusk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "forecast_weather_condition",
    primaryKeys = ["owner_location_id", "owner_dt", "position"],
    indices = [Index(value = ["owner_location_id"])],
    foreignKeys = [ForeignKey(
        entity = ForecastItemEntity::class,
        parentColumns = ["owner_location_id", "dt"],
        childColumns = ["owner_location_id", "owner_dt"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
        deferred = true
    )],
)
data class ForecastWeatherConditionEntity(

    /** FK → ForecastItemEntity.locationID. Part of composite PK. */
    @ColumnInfo(name = "owner_location_id") val ownerLocationId: String,

    /** FK → ForecastItemEntity.dt. Part of composite PK. */
    @ColumnInfo(name = "owner_dt") val ownerDt: Long,

    /** Index in the source weather[] array. 0 = primary condition. */
    @ColumnInfo(name = "position") val position: Int,

    @ColumnInfo(name = "condition_id") val conditionId: Int?,

    @ColumnInfo(name = "main") val main: String?,

    @ColumnInfo(name = "description") val description: String?,

    @ColumnInfo(name = "icon") val icon: String?
)