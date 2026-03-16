package iti.mad.dusk.ui.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import iti.mad.dusk.domain.exception.WeatherException
import iti.mad.dusk.domain.model.Location
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationManager @Inject constructor(@param:ApplicationContext private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 5000L
    ).apply {
        setMinUpdateIntervalMillis(2000L)
    }.build()

    companion object {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
        private const val UNKNOWN_CITY = "Unknown City"
        private const val UNKNOWN_COUNTRY = "Unknown Country"
    }

    sealed interface LocationResult {
        data class Success(val lat: Double, val lon: Double) : LocationResult
        data class Failure(val exception: WeatherException) : LocationResult
    }

    fun hasLocationPermission(): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            ) return true
        }
        return false
    }

    suspend fun checkGpsRequireResolution(): ResolvableApiException? = suspendCancellableCoroutine { cont ->
        val request = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            .setAlwaysShow(true).build()

        LocationServices.getSettingsClient(context).checkLocationSettings(request)
            .addOnSuccessListener {
                cont.resume(null)
            }.addOnFailureListener { exception ->
                val result = when ((exception as? ResolvableApiException)?.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> exception

                    else -> null
                }
                cont.resume(result)
            }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult = suspendCancellableCoroutine { cont ->
        val request = CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setDurationMillis(10_000L).setMaxUpdateAgeMillis(Long.MAX_VALUE).build()

        fusedLocationClient.getCurrentLocation(request, null).addOnSuccessListener { location ->
                if (location != null) cont.resume(
                    LocationResult.Success(
                        location.latitude,
                        location.longitude
                    )
                )
                else cont.resume(LocationResult.Failure(WeatherException.LocationUnavailable()))
            }.addOnFailureListener { exception ->
                cont.resume(LocationResult.Failure(WeatherException.LocationUnavailable(exception.message)))
            }
    }

    suspend fun buildLocation(
        lat: Double,
        lon: Double,
        isDefault: Boolean = false,
        isCurrent: Boolean = false,
    ): Location {
        val (cityName, country) = resolveAddress(lat, lon)
        val displayName = if (cityName != UNKNOWN_CITY) cityName else "$lat, $lon"

        return Location(
            lat = lat,
            lon = lon,
            cityName = cityName,
            country = country,
            displayName = displayName,
            isDefault = isDefault,
            isCurrent = isCurrent,
            addedAt = System.currentTimeMillis()
        )
    }

    private suspend fun resolveAddress(lat: Double, lon: Double): Pair<String, String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { cont ->
                geocoder.getFromLocation(lat, lon, 1) { addresses ->
                    val address = addresses.firstOrNull()
                    cont.resume(
                        (address?.locality ?: address?.subAdminArea
                        ?: UNKNOWN_CITY) to (address?.countryName ?: UNKNOWN_COUNTRY)
                    )
                }
            }
        } else {
            @Suppress("DEPRECATION")
            runCatching {
                geocoder.getFromLocation(
                    lat,
                    lon,
                    1
                )
            }.getOrNull()?.firstOrNull()?.let { address ->
                    (address.locality ?: address.subAdminArea
                    ?: UNKNOWN_CITY) to (address.countryName ?: UNKNOWN_COUNTRY)
                } ?: (UNKNOWN_CITY to UNKNOWN_COUNTRY)
        }
    }
}