package iti.mad.dusk.domain.repo

import iti.mad.dusk.domain.model.Language
import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WeatherSettings
import iti.mad.dusk.domain.model.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<WeatherSettings>
    suspend fun setTemperatureUnit(unit: TemperatureUnit)
    suspend fun setWindSpeedUnit(unit: WindSpeedUnit)
    suspend fun setLanguage(language: Language)
}