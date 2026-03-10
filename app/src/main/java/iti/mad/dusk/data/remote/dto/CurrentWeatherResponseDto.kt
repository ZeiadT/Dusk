package iti.mad.dusk.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Complete Retrofit DTO hierarchy for the OpenWeatherMap "Current Weather" API.
 * Endpoint: https://api.openweathermap.org/data/2.5/weather
 *
 * Every field is nullable to gracefully handle partial API responses.
 * The mapper applies safe defaults for any missing values.
 *
 * These classes exist ONLY in the data layer. They must NEVER leak into
 * the domain or presentation layers.
 */

// ── Root Response ──────────────────────────────────────────────────────

data class CurrentWeatherResponseDto(
    @SerializedName("coord")
    val coordinates: CoordinatesDto?,

    @SerializedName("weather")
    val weather: List<WeatherItemDto>?,

    @SerializedName("base")
    val base: String?,

    @SerializedName("main")
    val main: MainDto?,

    @SerializedName("visibility")
    val visibility: Int?,

    @SerializedName("wind")
    val wind: WindDto?,

    @SerializedName("rain")
    val rain: PrecipitationDto?,

    @SerializedName("snow")
    val snow: PrecipitationDto?,

    @SerializedName("clouds")
    val clouds: CloudsDto?,

    @SerializedName("dt")
    val dt: Long?,

    @SerializedName("sys")
    val sys: SysDto?,

    @SerializedName("timezone")
    val timezone: Int?,

    @SerializedName("id")
    val id: Long?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("cod")
    val cod: Int?
)

// ── Nested DTOs ────────────────────────────────────────────────────────

data class CoordinatesDto(
    @SerializedName("lon")
    val longitude: Double?,

    @SerializedName("lat")
    val latitude: Double?
)

/**
 * Weather condition descriptor.
 * A response may contain multiple items; the FIRST is the primary condition.
 *
 * The [id] field is the definitive weather classification:
 *   - 2xx: Thunderstorm
 *   - 3xx: Drizzle
 *   - 5xx: Rain
 *   - 6xx: Snow
 *   - 7xx: Atmosphere (haze, fog, mist, etc.)
 *   - 800: Clear
 *   - 80x: Clouds
 */
data class WeatherItemDto(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("main")
    val main: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("icon")
    val icon: String?
)

data class MainDto(
    @SerializedName("temp")
    val temp: Float?,

    @SerializedName("feels_like")
    val feelsLike: Float?,

    @SerializedName("temp_min")
    val tempMin: Float?,

    @SerializedName("temp_max")
    val tempMax: Float?,

    @SerializedName("pressure")
    val pressure: Int?,

    @SerializedName("humidity")
    val humidity: Int?,

    @SerializedName("sea_level")
    val seaLevel: Int?,

    @SerializedName("grnd_level")
    val groundLevel: Int?
)

data class WindDto(
    @SerializedName("speed")
    val speed: Float?,

    @SerializedName("deg")
    val deg: Int?,

    @SerializedName("gust")
    val gust: Float?
)

/**
 * Precipitation volume. Used for both rain and snow.
 * Keys "1h" and "3h" require @SerializedName because they start with digits.
 */
data class PrecipitationDto(
    @SerializedName("1h")
    val oneHour: Float?,

    @SerializedName("3h")
    val threeHour: Float?
)

data class CloudsDto(
    @SerializedName("all")
    val all: Int?
)

data class SysDto(
    @SerializedName("type")
    val type: Int?,

    @SerializedName("id")
    val id: Int?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("sunrise")
    val sunrise: Long?,

    @SerializedName("sunset")
    val sunset: Long?
)