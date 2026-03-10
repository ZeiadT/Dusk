package iti.mad.dusk.domain.repo

import iti.mad.dusk.core.base.Resource
import iti.mad.dusk.data.local.entity.ForecastCacheEntity
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.domain.model.Forecast
import kotlinx.coroutines.flow.Flow


interface WeatherRepository {
    fun getCurrentWeather(
        lat: Double,
        lon: Double,
        forceFresh: Boolean,
        language: String? = null
    ): Flow<Resource<CurrentWeather>>

    fun getForecast(
        lat: Double,
        lon: Double,
        forceFresh: Boolean,
        language: String? = null
    ): Flow<Resource<Forecast>>

    suspend fun clearExpiredCache()
}