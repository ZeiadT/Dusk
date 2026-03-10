package iti.mad.dusk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ══════════════════════════════════════════════════════════════════════════════
//  ForecastCacheEntity  —  one row per location, holds city metadata + TTL
//  PK: locationID = "$lat-$lon"
// ══════════════════════════════════════════════════════════════════════════════

const val FORECAST_TTL_MS = 10_800_000L   // 3 hours

@Entity(tableName = "forecast")
data class ForecastCacheEntity(

    @ColumnInfo(name = "lat") val lat: Double,

    @ColumnInfo(name = "lon") val lon: Double,

    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "locationID") val locationID: String,

    @ColumnInfo(name = "cached_at") val cachedAt: Long,

    // ── OWM response root ─────────────────────────────────────────────────────
    @ColumnInfo(name = "cod") val cod: String?,

    @ColumnInfo(name = "count") val count: Int?,

    // ── ForecastCityDto fields ────────────────────────────────────────────────
    @ColumnInfo(name = "city_id") val cityId: Long?,

    @ColumnInfo(name = "city_name") val cityName: String?,

    @ColumnInfo(name = "country") val country: String?,

    @ColumnInfo(name = "population") val population: Int?,

    @ColumnInfo(name = "timezone") val timezone: Int?,

    @ColumnInfo(name = "sunrise") val sunrise: Long?,

    @ColumnInfo(name = "sunset") val sunset: Long?,
) {
    companion object {
        fun locationId(lat: Double, lon: Double) = "$lat-$lon"
    }

}

fun ForecastCacheEntity.isFresh(): Boolean = System.currentTimeMillis() < cachedAt + FORECAST_TTL_MS


data class ForecastWithItems(
    val forecast: ForecastCacheEntity,
    val items: List<ForecastItemWithConditions>
)