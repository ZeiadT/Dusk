package iti.mad.dusk.data.local.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import iti.mad.dusk.domain.model.Language
import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WeatherSettings
import iti.mad.dusk.domain.model.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val KEY_TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val KEY_WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
        val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    val settingsFlow: Flow<WeatherSettings> = dataStore.data.catch { exception ->
        if (exception is IOException) emit(emptyPreferences())
        else throw exception
    }.map { prefs ->
        WeatherSettings(
            temperatureUnit = prefs[KEY_TEMPERATURE_UNIT]?.let { TemperatureUnit.valueOf(it) }
                ?: TemperatureUnit.CELSIUS,
            windSpeedUnit = prefs[KEY_WIND_SPEED_UNIT]?.let { WindSpeedUnit.valueOf(it) }
                ?: WindSpeedUnit.METERS_PER_SECOND,
            language = prefs[KEY_LANGUAGE]?.let { Language.valueOf(it) } ?: Language.ENGLISH,
        )
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        dataStore.edit { prefs ->
            prefs[KEY_TEMPERATURE_UNIT] = unit.name
        }
    }

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit) {
        dataStore.edit { prefs ->
            prefs[KEY_WIND_SPEED_UNIT] = unit.name
        }
    }

    suspend fun setLanguage(language: Language) {
        dataStore.edit { it[KEY_LANGUAGE] = language.name }
    }
}