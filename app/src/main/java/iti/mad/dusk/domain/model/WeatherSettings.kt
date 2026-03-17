package iti.mad.dusk.domain.model

enum class TemperatureUnit { CELSIUS, FAHRENHEIT }
enum class WindSpeedUnit { METERS_PER_SECOND, KILOMETERS_PER_HOUR }
enum class Language(val code: String) {
    ENGLISH("en"), ARABIC("ar")
}

data class WeatherSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METERS_PER_SECOND,
    val language: Language = Language.ENGLISH,
)