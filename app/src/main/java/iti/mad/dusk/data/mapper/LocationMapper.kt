package iti.mad.dusk.data.mapper

import iti.mad.dusk.data.local.entity.LocationEntity
import iti.mad.dusk.domain.model.Location

fun LocationEntity.toDomain(): Location = Location(
    lat = lat,
    lon = lon,
    cityName = cityName,
    country = country,
    displayName = displayName,
    isDefault = isDefault,
    addedAt = addedAt
)

fun Location.toEntity(): LocationEntity = LocationEntity(
    locationID = LocationEntity.locationId(lat, lon),
    lat = lat,
    lon = lon,
    cityName = cityName,
    country = country,
    displayName = displayName,
    isDefault = isDefault,
    addedAt = addedAt
)