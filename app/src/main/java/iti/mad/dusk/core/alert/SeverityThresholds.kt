package iti.mad.dusk.core.alert

object SeverityThresholds {
    val SEVERE_CONDITION_CODE_RANGES = listOf(
        200..232,
        300..321,
        500..531,
        600..622,
        701..781,
    )

    const val MAX_WIND_SPEED_MS = 10.0
    const val MIN_TEMPERATURE_CELSIUS = -10.0
    const val MAX_TEMPERATURE_CELSIUS = 40.0

    fun isSevereCondition(conditionCode: Int): Boolean =
        SEVERE_CONDITION_CODE_RANGES.any { conditionCode in it }

    fun isSevereWind(windSpeedMs: Double): Boolean =
        windSpeedMs > MAX_WIND_SPEED_MS

    fun isSevereTemperature(tempCelsius: Double): Boolean =
        tempCelsius < MIN_TEMPERATURE_CELSIUS || tempCelsius > MAX_TEMPERATURE_CELSIUS

    fun isSevere(conditionCode: Int, windSpeedMs: Double, tempCelsius: Double): Boolean =
        isSevereCondition(conditionCode)
            || isSevereWind(windSpeedMs)
            || isSevereTemperature(tempCelsius)
}