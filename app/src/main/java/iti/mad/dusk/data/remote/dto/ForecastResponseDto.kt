package iti.mad.dusk.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Retrofit DTO hierarchy for the OpenWeatherMap "5 Day / 3 Hour Forecast" API.
 * Endpoint: GET https://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}
 *
 * Response structure:
 * ```json
 * {
 *   "cod": "200",
 *   "message": 0,
 *   "cnt": 40,
 *   "list": [ ...40 forecast items... ],
 *   "city": { ...location metadata... }
 * }
 * ```
 *
 * All fields are nullable for graceful degradation on partial responses.
 * These DTOs exist ONLY in the data layer — never leaked to domain or UI.
 *
 * NOTE: Several nested DTOs (WeatherItemDto, MainDto, WindDto, CloudsDto,
 * PrecipitationDto, CoordinatesDto) are REUSED from CurrentWeatherResponseDto.kt
 * because the forecast list items share the same sub-structures.
 *
 */

// ── Root Forecast Response ─────────────────────────────────────────────

data class ForecastResponseDto(

    @SerializedName("cod")
    val cod: String?,

    @SerializedName("message")
    val message: Int?,

    /** Total number of forecast items in the [list]. Typically 40. */
    @SerializedName("cnt")
    val count: Int?,

    /** Array of 3-hour forecast data points. */
    @SerializedName("list")
    val list: List<ForecastItemDto>?,

    /** City/location metadata — shared across all forecast items. */
    @SerializedName("city")
    val city: ForecastCityDto?
)

// ── Individual Forecast Item (3-hour interval) ─────────────────────────

data class ForecastItemDto(

    /** Unix epoch timestamp (seconds) for this forecast interval. */
    @SerializedName("dt")
    val dt: Long?,

    /** Temperature, pressure, humidity. Same structure as current weather. */
    @SerializedName("main")
    val main: MainDto?,

    /** Weather condition descriptors. First entry is primary. */
    @SerializedName("weather")
    val weather: List<WeatherItemDto>?,

    /** Cloud coverage data. */
    @SerializedName("clouds")
    val clouds: CloudsDto?,

    /** Wind speed, direction, gusts. */
    @SerializedName("wind")
    val wind: WindDto?,

    /** Horizontal visibility in meters. */
    @SerializedName("visibility")
    val visibility: Int?,

    /** Probability of precipitation [0.0–1.0]. */
    @SerializedName("pop")
    val pop: Float?,

    /** Rainfall volume for the 3-hour interval. */
    @SerializedName("rain")
    val rain: PrecipitationDto?,

    /** Snowfall volume for the 3-hour interval. */
    @SerializedName("snow")
    val snow: PrecipitationDto?,

    /** System metadata. Contains `pod` (part of day: "d" or "n"). */
    @SerializedName("sys")
    val sys: ForecastSysDto?,

    /**
     * Human-readable date-time string in UTC.
     * Format: "2024-09-18 12:00:00"
     */
    @SerializedName("dt_txt")
    val dtTxt: String?
)

// ── Forecast-Specific Sys ──────────────────────────────────────────────
// Different from current weather's SysDto — forecast only has `pod`.

data class ForecastSysDto(
    /** Part of day: "d" = day, "n" = night. */
    @SerializedName("pod")
    val pod: String?
)

// ── City / Location Metadata ───────────────────────────────────────────

data class ForecastCityDto(

    @SerializedName("id")
    val id: Long?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("coord")
    val coord: CoordinatesDto?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("population")
    val population: Int?,

    /** UTC offset in seconds. Same semantics as current weather `timezone`. */
    @SerializedName("timezone")
    val timezone: Int?,

    @SerializedName("sunrise")
    val sunrise: Long?,

    @SerializedName("sunset")
    val sunset: Long?
)