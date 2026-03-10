package iti.mad.dusk.domain.exception

sealed class WeatherException() : Exception() {

    class NoNetwork : WeatherException()

    class LocationNotFound : WeatherException()

    class ServerError(val code: Int) : WeatherException()

    class Unknown(override val message: String?) : WeatherException()
}