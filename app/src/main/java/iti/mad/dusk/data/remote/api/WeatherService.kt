package iti.mad.dusk.data.remote.api

import iti.mad.dusk.core.network.ApiConstants
import iti.mad.dusk.data.remote.dto.ForecastResponseDto
import iti.mad.dusk.data.remote.dto.CurrentWeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {

    @GET(ApiConstants.Endpoints.CURRENT_WEATHER)
    suspend fun getCurrentWeather(
        @Query(ApiConstants.Params.LAT) latitude: Double,
        @Query(ApiConstants.Params.LON) longitude: Double,
        @Query(ApiConstants.Params.LANG) language: String
    ): CurrentWeatherResponseDto

    @GET(ApiConstants.Endpoints.FORECAST)
    suspend fun getForecast(
        @Query(ApiConstants.Params.LAT) latitude: Double,
        @Query(ApiConstants.Params.LON) longitude: Double,
        @Query(ApiConstants.Params.LANG) language: String
    ): ForecastResponseDto
}