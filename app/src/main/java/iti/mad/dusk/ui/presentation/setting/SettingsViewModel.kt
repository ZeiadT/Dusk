package iti.mad.dusk.ui.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iti.mad.dusk.domain.model.Language
import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WeatherSettings
import iti.mad.dusk.domain.model.WindSpeedUnit
import iti.mad.dusk.domain.repo.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<WeatherSettings> = settingsRepository.getSettings().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = WeatherSettings()
        )

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch { settingsRepository.setTemperatureUnit(unit) }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch { settingsRepository.setWindSpeedUnit(unit) }
    }


    fun setLanguage(language: Language) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }
}