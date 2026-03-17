package iti.mad.dusk.domain.exception

import android.content.Context
import iti.mad.dusk.R

fun WeatherException.toReadableMessage(context: Context): String = when (this) {
    is WeatherException.NoNetwork ->
        context.getString(R.string.error_no_network)

    is WeatherException.EndpointNotFound ->
        context.getString(R.string.error_endpoint_not_found)

    is WeatherException.LocationUnavailable ->
        message ?: context.getString(R.string.error_location_unavailable)

    is WeatherException.ServerError ->
        context.getString(R.string.error_server, code)

    is WeatherException.Unknown ->
        message ?: context.getString(R.string.error_unknown)
}