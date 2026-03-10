package iti.mad.dusk.core.network

import iti.mad.dusk.domain.exception.WeatherException
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        Result.success(apiCall())
    } catch (e: HttpException) {
        val error = when (e.code()) {
            404 -> WeatherException.LocationNotFound()
            in 500..599 -> WeatherException.ServerError(e.code())
            else -> WeatherException.Unknown(e.message)
        }
        Result.failure(error)
    } catch (e: IOException) {
        Result.failure(WeatherException.NoNetwork())
    } catch (e: Exception) {
        Result.failure(WeatherException.Unknown(e.message))
    }
}