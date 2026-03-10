package iti.mad.dusk.domain.model

data class Forecast(
    val latitude: Double,
    val longitude: Double,
    val city: ForecastCity,
    val fetchTimestampMillis: Long,
    val isFresh: Boolean,
    val hourly: List<ForecastItem>,
    val daily: List<DailyForecast>
)

data class ForecastCity(
    val name: String,
    val country: String,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class ForecastItem(
    val timestamp: Long,
    val dateText: String,
    val condition: WeatherCondition,
    val temperature: Temperature,
    val wind: Wind,
    val clouds: Int,
    val rain: Precipitation,
    val snow: Precipitation,
    val precipitationProbability: Float,
    val visibility: Int,
    val isDay: Boolean
)

data class DailyForecast(
    val date: String,
    val tempMin: Float,
    val tempMax: Float,
    val condition: WeatherCondition,
    val precipitationProbability: Float,
    val items: List<ForecastItem>
)