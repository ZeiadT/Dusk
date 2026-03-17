package iti.mad.dusk.ui.presentation.alert.model

import iti.mad.dusk.domain.model.WeatherAlert

data class AlertUiState(
    val activeAlerts: List<WeatherAlert> = emptyList(),
    val inactiveAlerts: List<WeatherAlert> = emptyList(),
)