package iti.mad.dusk.domain.repo

import iti.mad.dusk.domain.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun getAllAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertAlert(alert: WeatherAlert)
    suspend fun updateAlert(alert: WeatherAlert)
    suspend fun deleteAlert(id: Long)
    suspend fun setAlertActive(id: Long, active: Boolean)
}