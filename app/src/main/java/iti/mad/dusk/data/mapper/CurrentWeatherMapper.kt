package iti.mad.dusk.data.mapper

import iti.mad.dusk.core.util.extension.roundCoordinate
import iti.mad.dusk.data.local.entity.CurrentWeatherCacheEntity
import iti.mad.dusk.data.local.entity.CurrentWeatherConditionEntity
import iti.mad.dusk.data.local.entity.isFresh
import iti.mad.dusk.data.remote.dto.CurrentWeatherResponseDto
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.domain.model.Precipitation
import iti.mad.dusk.domain.model.SunCycle
import iti.mad.dusk.domain.model.Temperature
import iti.mad.dusk.domain.model.WeatherCondition
import iti.mad.dusk.domain.model.Wind

// ── DTO → Entity ──────────────────────────────────────────────────────────────

fun CurrentWeatherResponseDto.toEntity(lat: Double, lon: Double): CurrentWeatherCacheEntity {
    val roundedLat = lat.roundCoordinate()
    val roundedLon = lon.roundCoordinate()
    return CurrentWeatherCacheEntity(
        lat = roundedLat,
        lon = roundedLon,
        locationID = CurrentWeatherCacheEntity.locationId(roundedLat, roundedLon),
        cachedAt = System.currentTimeMillis(),
        cityId = id,
        cityName = name,
        country = sys?.country,
        timezone = timezone,
        dt = dt,
        sunrise = sys?.sunrise,
        sunset = sys?.sunset,
        visibility = visibility,
        base = base,
        cod = cod,
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
        rain1h = rain?.oneHour,
        rain3h = rain?.threeHour,
        snow1h = snow?.oneHour,
        snow3h = snow?.threeHour,
        sysType = sys?.type,
        sysId = sys?.id
    )
}

fun CurrentWeatherResponseDto.toConditionEntities(
    lat: Double,
    lon: Double
): List<CurrentWeatherConditionEntity> {
    val locationId = CurrentWeatherCacheEntity.locationId(
        lat.roundCoordinate(),
        lon.roundCoordinate()
    )
    return weather?.mapIndexed { index, item ->
        CurrentWeatherConditionEntity(
            ownerLocationId = locationId,
            position = index,
            conditionId = item.id,
            main = item.main,
            description = item.description,
            icon = item.icon
        )
    } ?: emptyList()
}

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun CurrentWeatherCacheEntity.toDomain(
    conditions: List<CurrentWeatherConditionEntity>
): CurrentWeather {
    val primary = conditions.firstOrNull()
    return CurrentWeather(
        latitude = lat,
        longitude = lon,
        cityName = cityName ?: "Unknown",
        country = country ?: "--",
        timezone = timezone ?: 0,
        fetchTimestampMillis = cachedAt,
        isFresh = isFresh(),

        condition = WeatherCondition(
            id = primary?.conditionId ?: 800,
            main = primary?.main ?: "Clear",
            description = primary?.description ?: "clear sky",
            icon = primary?.icon ?: "01d"
        ),

        temperature = Temperature(
            current = temp ?: 0f,
            feelsLike = feelsLike ?: 0f,
            min = tempMin ?: 0f,
            max = tempMax ?: 0f,
            pressure = pressure ?: 1013,
            humidity = humidity ?: 0
        ),

        wind = Wind(
            speed = windSpeed ?: 0f,
            degrees = windDeg ?: 0,
            gust = windGust ?: 0f
        ),

        cloudsInPercentage = cloudsAll ?: 0,

        rain = Precipitation(
            oneHour = rain1h ?: 0f,
            threeHour = rain3h ?: 0f
        ),

        snow = Precipitation(
            oneHour = snow1h ?: 0f,
            threeHour = snow3h ?: 0f
        ),

        sun = SunCycle(
            sunrise = sunrise ?: 0L,
            sunset = sunset ?: 0L
        )
    )
}