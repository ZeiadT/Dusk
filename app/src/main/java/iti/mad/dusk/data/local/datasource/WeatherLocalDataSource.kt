package iti.mad.dusk.data.local.datasource

import iti.mad.dusk.core.util.extension.roundCoordinate
import iti.mad.dusk.data.local.dao.CurrentWeatherDao
import iti.mad.dusk.data.local.entity.CURRENT_WEATHER_TTL_MS
import iti.mad.dusk.data.local.entity.CurrentWeatherCacheEntity
import iti.mad.dusk.data.local.entity.CurrentWeatherConditionEntity
import iti.mad.dusk.data.local.entity.CurrentWeatherWithConditions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class WeatherLocalDataSource @Inject constructor(
    private val weatherDao: CurrentWeatherDao
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCachedWeather(lat: Double, lon: Double): Flow<CurrentWeatherWithConditions?> =
        weatherDao.getWeatherByLocation(
            CurrentWeatherCacheEntity.locationId(
                lat.roundCoordinate(), lon.roundCoordinate()
            )
        ).mapLatest {
            if (it == null) return@mapLatest null
            val conditions = weatherDao.getConditionsByLocation(it.locationID)

            CurrentWeatherWithConditions(it, conditions)
        }


    suspend fun insertCache(
        entity: CurrentWeatherCacheEntity, conditions: List<CurrentWeatherConditionEntity>
    ) = weatherDao.insertWeatherWithConditions(entity, conditions)

    suspend fun deleteExpired() = weatherDao.clearExpired(
        System.currentTimeMillis(), CURRENT_WEATHER_TTL_MS
    )

}