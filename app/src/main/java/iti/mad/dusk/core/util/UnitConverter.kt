package iti.mad.dusk.core.util

import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WindSpeedUnit

object UnitConverter {

    fun convertTemperature(kelvin: Float, unit: TemperatureUnit): Float = when (unit) {
        TemperatureUnit.CELSIUS -> kelvin - 273.15f
        TemperatureUnit.FAHRENHEIT -> (kelvin - 273.15f) * 9f / 5f + 32f
    }

    fun convertWindSpeed(metersPerSecond: Float, unit: WindSpeedUnit): Float = when (unit) {
        WindSpeedUnit.METERS_PER_SECOND -> metersPerSecond
        WindSpeedUnit.KILOMETERS_PER_HOUR -> metersPerSecond * 3.6f
    }

    fun temperatureSymbol(unit: TemperatureUnit): String = when (unit) {
        TemperatureUnit.CELSIUS -> "°C"
        TemperatureUnit.FAHRENHEIT -> "°F"

    }

    fun windSpeedSymbol(unit: WindSpeedUnit): String = when (unit) {
        WindSpeedUnit.METERS_PER_SECOND -> "m/s"
        WindSpeedUnit.KILOMETERS_PER_HOUR -> "km/h"
    }

}
