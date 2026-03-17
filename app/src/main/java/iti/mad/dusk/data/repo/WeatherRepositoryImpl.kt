package iti.mad.dusk.data.repo

import iti.mad.dusk.core.base.Resource
import iti.mad.dusk.data.local.datasource.ForecastLocalDataSource
import iti.mad.dusk.data.local.datasource.WeatherLocalDataSource
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
import iti.mad.dusk.domain.repo.SettingsRepository
import iti.mad.dusk.domain.repo.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localWeatherDataSource: WeatherLocalDataSource,
    private val localForecastDataSource: ForecastLocalDataSource,
    private val settingsRepository: SettingsRepository,
) : WeatherRepository {

    override fun getCurrentWeather(
        lat: Double, lon: Double, forceFresh: Boolean, language: String?
    ): Flow<Resource<CurrentWeather>> {
        var consumedForceFresh = forceFresh

        return settingsRepository.getSettings().flatMapLatest { settings ->
            localWeatherDataSource.getCachedWeather(lat, lon).flatMapLatest { cached ->
                val domain = cached?.weather?.toDomain(cached.conditions, settings)

                when {
                    !consumedForceFresh && domain != null && cached.weather.isFresh() -> flowOf(
                        Resource.Success(domain)
                    )

                    else -> flow {
                        if (domain != null) emit(Resource.Success(domain))
                        emit(Resource.Loading)

                        remoteDataSource.getCurrentWeather(lat, lon, language)
                            .onSuccess { dto ->
                                consumedForceFresh = false
                                val entity = dto.toEntity(lat, lon)
                                val conditions = dto.toConditionEntities(lat, lon)
                                localWeatherDataSource.insertCache(entity, conditions)
                            }
                            .onFailure { throwable ->
                                emit(Resource.Failure(throwable as WeatherException))
                            }
                    }
                }
            }
        }
    }

    override fun getForecast(
        lat: Double, lon: Double, forceFresh: Boolean, language: String?
    ): Flow<Resource<Forecast>> {
        var consumedForceFresh = forceFresh

        return settingsRepository.getSettings().flatMapLatest { settings ->
            localForecastDataSource.getForecast(lat, lon).flatMapLatest { cached ->
                val domain = cached?.forecast?.toDomain(cached.items, settings)

                when {
                    !consumedForceFresh && domain != null && cached.forecast.isFresh() -> flowOf(
                        Resource.Success(domain)
                    )

                    else -> flow {
                        if (domain != null) emit(Resource.Success(domain))
                        emit(Resource.Loading)

                        remoteDataSource.getForecast(lat, lon, language)
                            .onSuccess { dto ->
                                consumedForceFresh = false
                                val cache = dto.toCacheEntity(lat, lon)
                                val items = dto.toItemEntities(lat, lon)
                                val conditions = dto.toConditionEntities(lat, lon)
                                localForecastDataSource.insertCache(cache, items, conditions)
                            }
                            .onFailure { throwable ->
                                emit(Resource.Failure(throwable as WeatherException))
                            }
                    }
                }
            }
        }
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