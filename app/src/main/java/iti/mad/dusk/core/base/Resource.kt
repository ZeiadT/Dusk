package iti.mad.dusk.core.base

import iti.mad.dusk.domain.exception.WeatherException

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Failure(val exception: WeatherException) : Resource<Nothing>()
}