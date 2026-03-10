package iti.mad.dusk.data.local.datasource

import iti.mad.dusk.core.util.extension.roundCoordinate
import iti.mad.dusk.data.local.dao.ForecastDao
import iti.mad.dusk.data.local.entity.FORECAST_TTL_MS
import iti.mad.dusk.data.local.entity.ForecastCacheEntity
import iti.mad.dusk.data.local.entity.ForecastItemEntity
import iti.mad.dusk.data.local.entity.ForecastItemWithConditions
import iti.mad.dusk.data.local.entity.ForecastWeatherConditionEntity
import iti.mad.dusk.data.local.entity.ForecastWithItems
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class ForecastLocalDataSource @Inject constructor(private val forecastDao: ForecastDao) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getForecast(lat: Double, lon: Double): Flow<ForecastWithItems?> =
        forecastDao.getForecastByLocation(
            ForecastCacheEntity.locationId(
                lat.roundCoordinate(), lon.roundCoordinate()
            )
        ).mapLatest {
            if (it == null) return@mapLatest null
            val items = forecastDao.getItemsByLocation(it.locationID)
            val conditions = forecastDao.getConditionsByLocation(it.locationID)

            val conditionsByDt = conditions.groupBy { condition -> condition.ownerDt }

            val itemsWithConditions = items.map { item ->
                ForecastItemWithConditions(
                    item = item, conditions = conditionsByDt[item.dt] ?: emptyList()
                )
            }

            ForecastWithItems(it, itemsWithConditions)
        }


    suspend fun insertCache(
        entity: ForecastCacheEntity,
        items: List<ForecastItemEntity>,
        conditions: List<ForecastWeatherConditionEntity>
    ) = forecastDao.insertForecastWithItems(entity, items, conditions)

    suspend fun deleteExpired() = forecastDao.clearExpired(
        System.currentTimeMillis(), FORECAST_TTL_MS
    )
}