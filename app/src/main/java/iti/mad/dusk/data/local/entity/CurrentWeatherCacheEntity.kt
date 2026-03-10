package iti.mad.dusk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

const val CURRENT_WEATHER_TTL_MS = 3_600_000L   // 1 hour

@Entity(tableName = "current_weather")
data class CurrentWeatherCacheEntity(

    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double,

    // ── PK (location) ───────────────────────────────────────────────
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "locationID")
    val locationID: String,

    // ── Cache bookkeeping ─────────────────────────────────────────────────────
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long,

    // ── API response fields ───────────────────────────────────────────────────
    /** OpenWeatherMap internal city ID. */
    @ColumnInfo(name = "city_id")
    val cityId: Long?,

    /** City name returned by the API. */
    @ColumnInfo(name = "city_name")
    val cityName: String?,

    /** ISO 3166-1 alpha-2 country code (e.g. "EG"). */
    @ColumnInfo(name = "country")
    val country: String?,

    /**
     * UTC offset of the location in seconds.
     * Add to a UTC epoch to get local time.
     */
    @ColumnInfo(name = "timezone")
    val timezone: Int?,

    /** Unix epoch (seconds) of the observation. */
    @ColumnInfo(name = "dt")
    val dt: Long?,

    /** Sunrise time — Unix epoch (seconds). */
    @ColumnInfo(name = "sunrise")
    val sunrise: Long?,

    /** Sunset time — Unix epoch (seconds). */
    @ColumnInfo(name = "sunset")
    val sunset: Long?,

    /** Horizontal visibility in meters (max 10 000). */
    @ColumnInfo(name = "visibility")
    val visibility: Int?,

    /** Measurement station network used by OWM. */
    @ColumnInfo(name = "base")
    val base: String?,

    /** HTTP-like status code from OWM (200 = success). */
    @ColumnInfo(name = "cod")
    val cod: Int?,

    // ── Embedded: MainDto fields ───────────────────────────────────────────────
    /** Current temperature in the requested unit. */
    @ColumnInfo(name = "temp")
    val temp: Float?,

    /** "Feels like" temperature. */
    @ColumnInfo(name = "feels_like")
    val feelsLike: Float?,

    /** Minimum observed temperature within a large city/area. */
    @ColumnInfo(name = "temp_min")
    val tempMin: Float?,

    /** Maximum observed temperature within a large city/area. */
    @ColumnInfo(name = "temp_max")
    val tempMax: Float?,

    /** Atmospheric pressure at station level (hPa). */
    @ColumnInfo(name = "pressure")
    val pressure: Int?,

    /** Relative humidity (%). */
    @ColumnInfo(name = "humidity")
    val humidity: Int?,

    /** Pressure at sea level (hPa). */
    @ColumnInfo(name = "sea_level")
    val seaLevel: Int?,

    /** Pressure at ground level (hPa). */
    @ColumnInfo(name = "ground_level")
    val groundLevel: Int?,

    // ── Embedded: WindDto fields ───────────────────────────────────────────────
    /** Wind speed in m/s (or mph if imperial was requested). */
    @ColumnInfo(name = "wind_speed")
    val windSpeed: Float?,

    /** Wind direction in meteorological degrees (0–360). */
    @ColumnInfo(name = "wind_deg")
    val windDeg: Int?,

    /** Wind gust speed. */
    @ColumnInfo(name = "wind_gust")
    val windGust: Float?,

    // ── Embedded: CloudsDto fields ─────────────────────────────────────────────
    /** Cloud coverage (%). */
    @ColumnInfo(name = "clouds_all")
    val cloudsAll: Int?,

    // ── Embedded: PrecipitationDto (rain) ─────────────────────────────────────
    /** Rain volume for the last 1 hour (mm). */
    @ColumnInfo(name = "rain_1h")
    val rain1h: Float?,

    /** Rain volume for the last 3 hours (mm). */
    @ColumnInfo(name = "rain_3h")
    val rain3h: Float?,

    // ── Embedded: PrecipitationDto (snow) ─────────────────────────────────────
    /** Snow volume for the last 1 hour (mm). */
    @ColumnInfo(name = "snow_1h")
    val snow1h: Float?,

    /** Snow volume for the last 3 hours (mm). */
    @ColumnInfo(name = "snow_3h")
    val snow3h: Float?,

    // ── Embedded: SysDto fields ────────────────────────────────────────────────
    /** OWM internal parameter. */
    @ColumnInfo(name = "sys_type")
    val sysType: Int?,

    /** OWM internal station ID. */
    @ColumnInfo(name = "sys_id")
    val sysId: Int?

){
    companion object {
        fun locationId(lat: Double, lon: Double) = "$lat-$lon"
    }
}

fun CurrentWeatherCacheEntity.isFresh(): Boolean =
    System.currentTimeMillis() < cachedAt + CURRENT_WEATHER_TTL_MS

data class CurrentWeatherWithConditions(

    @Embedded
    val weather: CurrentWeatherCacheEntity,

    @Relation(
        parentColumn = "locationID",
        entityColumn = "owner_location_id",
    )
    val conditions: List<CurrentWeatherConditionEntity>
)