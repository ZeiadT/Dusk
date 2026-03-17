package iti.mad.dusk.data.mapper

import iti.mad.dusk.core.util.UnitConverter
import iti.mad.dusk.core.util.extension.roundCoordinate
import iti.mad.dusk.data.local.entity.ForecastCacheEntity
import iti.mad.dusk.data.local.entity.ForecastItemEntity
import iti.mad.dusk.data.local.entity.ForecastItemWithConditions
import iti.mad.dusk.data.local.entity.ForecastWeatherConditionEntity
import iti.mad.dusk.data.local.entity.isFresh
import iti.mad.dusk.data.remote.dto.ForecastItemDto
import iti.mad.dusk.data.remote.dto.ForecastResponseDto
import iti.mad.dusk.domain.model.DailyForecast
import iti.mad.dusk.domain.model.Forecast
import iti.mad.dusk.domain.model.ForecastCity
import iti.mad.dusk.domain.model.ForecastItem
import iti.mad.dusk.domain.model.Precipitation
import iti.mad.dusk.domain.model.Temperature
import iti.mad.dusk.domain.model.WeatherCondition
import iti.mad.dusk.domain.model.WeatherSettings
import iti.mad.dusk.domain.model.Wind

// ── DTO → Entity ──────────────────────────────────────────────────────────────

fun ForecastResponseDto.toCacheEntity(lat: Double, lon: Double): ForecastCacheEntity {
    val roundedLat = lat.roundCoordinate()
    val roundedLon = lon.roundCoordinate()
    return ForecastCacheEntity(
        lat = roundedLat,
        lon = roundedLon,
        locationID = ForecastCacheEntity.locationId(roundedLat, roundedLon),
        cachedAt = System.currentTimeMillis(),
        cod = cod,
        count = count,
        cityId = city?.id,
        cityName = city?.name,
        country = city?.country,
        population = city?.population,
        timezone = city?.timezone,
        sunrise = city?.sunrise,
        sunset = city?.sunset
    )
}

fun ForecastResponseDto.toItemEntities(lat: Double, lon: Double): List<ForecastItemEntity> {
    val locationId = ForecastCacheEntity.locationId(
        lat.roundCoordinate(),
        lon.roundCoordinate()
    )
    return list?.map { it.toItemEntity(locationId) } ?: emptyList()
}

fun ForecastResponseDto.toConditionEntities(
    lat: Double,
    lon: Double
): List<ForecastWeatherConditionEntity> {
    val locationId = ForecastCacheEntity.locationId(
        lat.roundCoordinate(),
        lon.roundCoordinate()
    )
    return list?.flatMap { item ->
        item.weather?.mapIndexed { index, condition ->
            ForecastWeatherConditionEntity(
                ownerLocationId = locationId,
                ownerDt = item.dt ?: 0L,
                position = index,
                conditionId = condition.id,
                main = condition.main,
                description = condition.description,
                icon = condition.icon
            )
        } ?: emptyList()
    } ?: emptyList()
}

private fun ForecastItemDto.toItemEntity(locationId: String): ForecastItemEntity =
    ForecastItemEntity(
        ownerLocationId = locationId,
        dt = dt ?: 0L,
        dtTxt = dtTxt,
        pod = sys?.pod,
        temp = main?.temp,
        feelsLike = main?.feelsLike,
        tempMin = main?.tempMin,
        tempMax = main?.tempMax,
        pressure = main?.pressure,
        humidity = main?.humidity,
        seaLevel = main?.seaLevel,
        groundLevel = main?.groundLevel,
        windSpeed = wind?.speed,
        windDeg = wind?.deg,
        windGust = wind?.gust,
        cloudsAll = clouds?.all,
        visibility = visibility,
        pop = pop,
        rain1h = rain?.oneHour,
        rain3h = rain?.threeHour,
        snow1h = snow?.oneHour,
        snow3h = snow?.threeHour
    )

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun ForecastCacheEntity.toDomain(
    items: List<ForecastItemWithConditions>,
    weatherSettings: WeatherSettings
): Forecast {
    val forecastItems = items.map { it.toDomain(weatherSettings) }
    return Forecast(
        latitude = lat,
        longitude = lon,
        fetchTimestampMillis = cachedAt,
        isFresh = isFresh(),
        city = ForecastCity(
            name = cityName ?: "Unknown",
            country = country ?: "--",
            timezone = timezone ?: 0,
            sunrise = sunrise ?: 0L,
            sunset = sunset ?: 0L
        ),
        hourly = forecastItems,
        daily = forecastItems.groupByDay()
    )
}

private fun ForecastItemWithConditions.toDomain(weatherSettings: WeatherSettings): ForecastItem {
    val primary = conditions.firstOrNull()
    return ForecastItem(
        timestamp = item.dt,
        dateText = item.dtTxt ?: "",
        isDay = item.pod == "d",
        visibility = item.visibility ?: 0,
        precipitationProbability = item.pop ?: 0f,
        clouds = item.cloudsAll ?: 0,
        condition = WeatherCondition(
            id = primary?.conditionId ?: 800,
            main = primary?.main ?: "Clear",
            description = primary?.description ?: "clear sky",
            icon = primary?.icon ?: "01d"
        ),
        temperature = Temperature(
            current   = UnitConverter.convertTemperature(item.temp      ?: 0f, weatherSettings.temperatureUnit),
            feelsLike = UnitConverter.convertTemperature(item.feelsLike ?: 0f, weatherSettings.temperatureUnit),
            min       = UnitConverter.convertTemperature(item.tempMin   ?: 0f, weatherSettings.temperatureUnit),
            max       = UnitConverter.convertTemperature(item.tempMax   ?: 0f, weatherSettings.temperatureUnit),
            pressure  = item.pressure ?: 1013,
            humidity  = item.humidity ?: 0
        ),
        wind = Wind(
            speed   = UnitConverter.convertWindSpeed(item.windSpeed ?: 0f, weatherSettings.windSpeedUnit),
            degrees = item.windDeg ?: 0,
            gust    = UnitConverter.convertWindSpeed(item.windGust ?: 0f, weatherSettings.windSpeedUnit),
        ),
        rain = Precipitation(
            oneHour   = item.rain1h ?: 0f,
            threeHour = item.rain3h ?: 0f
        ),
        snow = Precipitation(
            oneHour   = item.snow1h ?: 0f,
            threeHour = item.snow3h ?: 0f
        )
    )
}

// ── Domain helpers ────────────────────────────────────────────────────────────

private fun List<ForecastItem>.groupByDay(): List<DailyForecast> =
    groupBy { it.dateText.take(10) }
        .map { (date, items) ->
            DailyForecast(
                date = date,
                tempMin = items.minOf { it.temperature.min },
                tempMax = items.maxOf { it.temperature.max },
                precipitationProbability = items.maxOf { it.precipitationProbability },
                condition = items.groupBy { it.condition.id }
                    .maxBy { it.value.size }.value.first().condition,
                items = items
            )
        }
        .sortedBy { it.date }