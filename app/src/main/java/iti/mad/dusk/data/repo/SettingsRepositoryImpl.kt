package iti.mad.dusk.data.repo

import iti.mad.dusk.data.local.datasource.SettingsDataStore
import iti.mad.dusk.domain.model.Language
import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WeatherSettings
import iti.mad.dusk.domain.model.WindSpeedUnit
import iti.mad.dusk.domain.repo.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val dataStore: SettingsDataStore)
    : SettingsRepository {

    override fun getSettings(): Flow<WeatherSettings> = dataStore.settingsFlow

    override suspend fun setTemperatureUnit(unit: TemperatureUnit) =
        dataStore.setTemperatureUnit(unit)

    override suspend fun setWindSpeedUnit(unit: WindSpeedUnit) = dataStore.setWindSpeedUnit(unit)

    override suspend fun setLanguage(language: Language) =
        dataStore.setLanguage(language)
}