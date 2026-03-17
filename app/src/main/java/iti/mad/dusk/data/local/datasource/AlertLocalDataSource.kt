package iti.mad.dusk.data.local.datasource

import iti.mad.dusk.data.local.dao.AlertDao
import iti.mad.dusk.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlertLocalDataSource @Inject constructor(
    private val alertDao: AlertDao,
) {
    fun getAllAlerts(): Flow<List<AlertEntity>> = alertDao.getAllAlerts()

    suspend fun insertAlert(alert: AlertEntity) = alertDao.insertAlert(alert)

    suspend fun updateAlert(alert: AlertEntity) = alertDao.updateAlert(alert)

    suspend fun deleteAlert(id: Long) = alertDao.deleteAlert(id)

    suspend fun setAlertActive(id: Long, active: Boolean) = alertDao.setAlertActive(id, active)

    suspend fun getAlertById(id: Long) = alertDao.getAlertById(id)
}
