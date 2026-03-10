package iti.mad.dusk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "forecast_item",
    primaryKeys = ["owner_location_id", "dt"],
    indices = [Index(value = ["owner_location_id"])],
    foreignKeys = [ForeignKey(
        entity = ForecastCacheEntity::class,
        parentColumns = ["locationID"],
        childColumns = ["owner_location_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
        deferred = true
    )],
)
data class ForecastItemEntity(

    /** FK → ForecastCacheEntity.locationID. Also part of composite PK. */
    @ColumnInfo(name = "owner_location_id") val ownerLocationId: String,

    /** Unix epoch (seconds) of this 3-hour interval. Part of composite PK. */
    @ColumnInfo(name = "dt") val dt: Long,

    /** UTC date-time string, e.g. "2024-09-18 12:00:00". */
    @ColumnInfo(name = "dt_txt") val dtTxt: String?,

    /** Part of day: "d" = daytime, "n" = night-time. */
    @ColumnInfo(name = "pod") val pod: String?,

    // ── MainDto ───────────────────────────────────────────────────────────────
    @ColumnInfo(name = "temp") val temp: Float?,

    @ColumnInfo(name = "feels_like") val feelsLike: Float?,

    @ColumnInfo(name = "temp_min") val tempMin: Float?,

    @ColumnInfo(name = "temp_max") val tempMax: Float?,

    @ColumnInfo(name = "pressure") val pressure: Int?,

    @ColumnInfo(name = "humidity") val humidity: Int?,

    @ColumnInfo(name = "sea_level") val seaLevel: Int?,

    @ColumnInfo(name = "ground_level") val groundLevel: Int?,

    // ── WindDto ───────────────────────────────────────────────────────────────
    @ColumnInfo(name = "wind_speed") val windSpeed: Float?,

    @ColumnInfo(name = "wind_deg") val windDeg: Int?,

    @ColumnInfo(name = "wind_gust") val windGust: Float?,

    // ── CloudsDto ─────────────────────────────────────────────────────────────
    @ColumnInfo(name = "clouds_all") val cloudsAll: Int?,

    // ── Visibility ────────────────────────────────────────────────────────────
    @ColumnInfo(name = "visibility") val visibility: Int?,

    // ── Probability of precipitation [0.0–1.0] ────────────────────────────────
    @ColumnInfo(name = "pop") val pop: Float?,

    // ── PrecipitationDto (rain) ───────────────────────────────────────────────
    @ColumnInfo(name = "rain_1h") val rain1h: Float?,

    @ColumnInfo(name = "rain_3h") val rain3h: Float?,

    // ── PrecipitationDto (snow) ───────────────────────────────────────────────
    @ColumnInfo(name = "snow_1h") val snow1h: Float?,

    @ColumnInfo(name = "snow_3h") val snow3h: Float?
)


data class ForecastItemWithConditions(
    val item: ForecastItemEntity,
    val conditions: List<ForecastWeatherConditionEntity>
)