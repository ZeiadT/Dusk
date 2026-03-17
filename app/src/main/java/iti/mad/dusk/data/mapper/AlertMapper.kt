package iti.mad.dusk.data.mapper

import iti.mad.dusk.data.local.entity.AlertEntity
import iti.mad.dusk.domain.model.WeatherAlert

fun AlertEntity.toDomain() = WeatherAlert(
    id = id,
    fromHour = fromHour,
    fromMinute = fromMinute,
    toHour = toHour,
    toMinute = toMinute,
    type = type,
    isActive = isActive,
)

fun WeatherAlert.toEntity() = AlertEntity(
    id = id,
    fromHour = fromHour,
    fromMinute = fromMinute,
    toHour = toHour,
    toMinute = toMinute,
    type = type,
    isActive = isActive,
)
