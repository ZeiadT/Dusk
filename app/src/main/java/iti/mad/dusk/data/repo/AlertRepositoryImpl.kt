package iti.mad.dusk.data.repo

import iti.mad.dusk.core.alert.AlertScheduler
import iti.mad.dusk.data.local.datasource.AlertLocalDataSource
import iti.mad.dusk.data.mapper.toDomain
import iti.mad.dusk.data.mapper.toEntity
import iti.mad.dusk.domain.model.WeatherAlert
import iti.mad.dusk.domain.repo.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val localDataSource: AlertLocalDataSource,
    private val alertScheduler: AlertScheduler,
) : AlertRepository {

    override fun getAllAlerts(): Flow<List<WeatherAlert>> =
        localDataSource.getAllAlerts().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertAlert(alert: WeatherAlert) {
        localDataSource.insertAlert(alert.toEntity())
        alertScheduler.schedule(alert)
    }

    override suspend fun updateAlert(alert: WeatherAlert) {
        alertScheduler.cancel(alert.id)
        localDataSource.updateAlert(alert.toEntity())
        alertScheduler.schedule(alert)
    }

    override suspend fun deleteAlert(id: Long) {
        alertScheduler.cancel(id)
        localDataSource.deleteAlert(id)
    }

    override suspend fun setAlertActive(id: Long, active: Boolean) {
        localDataSource.setAlertActive(id, active)
        if (active) {
            localDataSource.getAlertById(id)?.toDomain()?.let { alertScheduler.schedule(it) }
        } else {
            alertScheduler.cancel(id)
        }
    }
}
