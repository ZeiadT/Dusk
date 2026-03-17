package iti.mad.dusk.domain.model

data class WeatherAlert(
    val id: Long = 0,
    val fromHour: Int,
    val fromMinute: Int,
    val toHour: Int,
    val toMinute: Int,
    val type: AlertType,
    val isActive: Boolean = true,
)

enum class AlertType { NOTIFICATION, ALARM }
