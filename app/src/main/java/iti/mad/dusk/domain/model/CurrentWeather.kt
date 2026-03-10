package iti.mad.dusk.domain.model

data class CurrentWeather(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val country: String,
    val timezone: Int,
    val fetchTimestampMillis: Long,
    val condition: WeatherCondition,
    val temperature: Temperature,
    val wind: Wind,
    val cloudsInPercentage: Int,
    val rain: Precipitation,
    val snow: Precipitation,
    val sun: SunCycle,
    val isFresh: Boolean
)

data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Temperature(
    val current: Float,
    val feelsLike: Float,
    val min: Float,
    val max: Float,
    val pressure: Int,
    val humidity: Int
)

data class Wind(
    val speed: Float,
    val degrees: Int,
    val gust: Float
)

data class Precipitation(
    val oneHour: Float,
    val threeHour: Float
)

data class SunCycle(
    val sunrise: Long,
    val sunset: Long
)