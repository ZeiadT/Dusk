package iti.mad.dusk.ui.presentation.home.model

import iti.mad.dusk.domain.exception.WeatherException
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.domain.model.Forecast
import iti.mad.dusk.domain.model.WeatherSettings

data class HomeUiState(
    val currentWeather: CurrentWeather? = null,
    val forecast: Forecast? = null,
    val weatherException: WeatherException? = null,
    val forecastException: WeatherException? = null,
    val isRefreshing: Boolean = false,
    val isInitialized: Boolean = false,
    val settings: WeatherSettings = WeatherSettings(),
)