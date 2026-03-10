package iti.mad.dusk.data.repo

import iti.mad.dusk.core.base.Resource
import iti.mad.dusk.data.local.datasource.ForecastLocalDataSource
import iti.mad.dusk.data.local.datasource.WeatherLocalDataSource
import iti.mad.dusk.data.local.entity.ForecastItemWithConditions
import iti.mad.dusk.data.local.entity.isFresh
import iti.mad.dusk.data.mapper.toCacheEntity
import iti.mad.dusk.data.mapper.toConditionEntities
import iti.mad.dusk.data.mapper.toDomain
import iti.mad.dusk.data.mapper.toEntity
import iti.mad.dusk.data.mapper.toItemEntities
import iti.mad.dusk.data.remote.datasource.WeatherRemoteDataSource
import iti.mad.dusk.domain.exception.WeatherException
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.domain.model.Forecast
import iti.mad.dusk.domain.repo.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localWeatherDataSource: WeatherLocalDataSource,
    private val localForecastDataSource: ForecastLocalDataSource
) : WeatherRepository {

    override fun getCurrentWeather(
        lat: Double, lon: Double, forceFresh: Boolean, language: String?
    ): Flow<Resource<CurrentWeather>> = flow {

        emit(Resource.Loading)

        if (forceFresh.not()) {
            val cached = localWeatherDataSource.getCachedWeather(lat, lon).firstOrNull()
            if (cached != null) {
                emit(Resource.Success(cached.weather.toDomain(cached.conditions)))

                if (cached.weather.isFresh()) return@flow
            }
        }

        val fresh = remoteDataSource.getCurrentWeather(lat, lon, language)
        fresh.onSuccess { dto ->

            val entity = dto.toEntity(lat, lon)
            val conditions = dto.toConditionEntities(lat, lon)

            localWeatherDataSource.insertCache(entity, conditions)

            emit(Resource.Success(entity.toDomain(conditions)))

        }.onFailure { throwable ->
            emit(Resource.Failure(throwable as WeatherException))
        }
    }

    override fun getForecast(
        lat: Double, lon: Double, forceFresh: Boolean, language: String?
    ): Flow<Resource<Forecast>> = flow {
        emit(Resource.Loading)

        if (forceFresh.not()) {
            val cached = localForecastDataSource.getForecast(lat, lon).firstOrNull()
            if (cached != null) {
                emit(Resource.Success(cached.forecast.toDomain(cached.items)))

                if (cached.forecast.isFresh()) return@flow
            }
        }

        val fresh = remoteDataSource.getForecast(lat, lon, language)
        fresh.onSuccess { dto ->

            val cache = dto.toCacheEntity(lat, lon)
            val items = dto.toItemEntities(lat, lon)
            val conditions = dto.toConditionEntities(lat, lon)

            localForecastDataSource.insertCache(cache, items, conditions)

            val conditionsByDt = conditions.groupBy { it.ownerDt }
            val itemsWithConditions = items.map { item ->
                ForecastItemWithConditions(item, conditionsByDt[item.dt] ?: emptyList())
            }
            emit(Resource.Success(cache.toDomain(itemsWithConditions)))
        }.onFailure { throwable -> emit(Resource.Failure(throwable as WeatherException)) }
    }

    override suspend fun clearExpiredCache() {
        try {
            localForecastDataSource.deleteExpired()
            localWeatherDataSource.deleteExpired()
        } catch (e: Exception) {
            throw WeatherException.Unknown("Failed to clear expired cache: ${e.message}")
        }
    }
}