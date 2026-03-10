package iti.mad.dusk.data.remote.datasource

import iti.mad.dusk.core.network.safeApiCall
import iti.mad.dusk.data.remote.api.WeatherService
import iti.mad.dusk.data.remote.dto.CurrentWeatherResponseDto
import iti.mad.dusk.data.remote.dto.ForecastResponseDto
import javax.inject.Inject

class WeatherRemoteDataSource @Inject constructor(private val weatherService: WeatherService) {
    suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, language: String?
    ): Result<CurrentWeatherResponseDto> =
        safeApiCall { weatherService.getCurrentWeather(latitude, longitude, language ?: "en") }

    suspend fun getForecast(
        latitude: Double, longitude: Double, language: String?
    ): Result<ForecastResponseDto> =
        safeApiCall { weatherService.getForecast(latitude, longitude, language ?: "en") }
}