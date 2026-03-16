package iti.mad.dusk.ui.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iti.mad.dusk.core.base.Resource
import iti.mad.dusk.core.util.extension.combine
import iti.mad.dusk.core.util.extension.roundCoordinate
import iti.mad.dusk.domain.exception.WeatherException
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.domain.model.Forecast
import iti.mad.dusk.domain.repo.LocationRepository
import iti.mad.dusk.domain.repo.WeatherRepository
import iti.mad.dusk.ui.presentation.home.model.HomeUiState
import iti.mad.dusk.ui.presentation.home.model.LocationEvent
import iti.mad.dusk.ui.util.LocationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val locationManager: LocationManager,
) : ViewModel() {

    private var weatherJob: Job? = null
    private var lastKnownLocation: Pair<Double, Double>? = null
        set(value) {
            if(value == null) {
                field = null
                return
            }

            val lat = value.first.roundCoordinate()
            val lon = value.second.roundCoordinate()

            field = lat to lon
        }

    private val _currentWeatherState = MutableStateFlow<CurrentWeather?>(null)
    private val _forecastState = MutableStateFlow<Forecast?>(null)
    private val _weatherException = MutableStateFlow<WeatherException?>(null)
    private val _forecastException = MutableStateFlow<WeatherException?>(null)
    private val _weatherLoading = MutableStateFlow(false)
    private val _forecastLoading = MutableStateFlow(false)
    private val _isInitialized = MutableStateFlow(false)

    private val _locationEvents = Channel<LocationEvent>()
    val locationEvents = _locationEvents.receiveAsFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        _currentWeatherState,
        _forecastState,
        _weatherException,
        _forecastException,
        _weatherLoading,
        _forecastLoading,
        _isInitialized
    ) { weather, forecast, weatherError, forecastError, weatherLoading, forecastLoading, initialized ->
        HomeUiState(
            currentWeather = weather,
            forecast = forecast,
            weatherException = weatherError,
            forecastException = forecastError,
            isRefreshing = weatherLoading || forecastLoading,
            isInitialized = initialized
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            locationRepository.getDefaultLocation().collect { location ->
                Log.d("TAG", "Default Location: $location")

                if (location == null || location.isCurrent) {
                    Log.d("TAG", "Location is null or current")
                    if (location != null && lastKnownLocation != null && (location.lat == lastKnownLocation?.first && location.lon == lastKnownLocation?.second)) {
                        Log.d("TAG", "Current location found")
                        fetchAndUpdateUI(location.lat, location.lon, forceFresh = false)
                        return@collect
                    }

                    Log.d("TAG", "Requesting permission")
                    if(location != null) lastKnownLocation = location.lat to location.lon
                    _locationEvents.send(LocationEvent.RequestPermissions)
                    return@collect
                }

                Log.d("TAG", "Location is not null nor current")
                lastKnownLocation = location.lat to location.lon
                fetchAndUpdateUI(location.lat, location.lon, forceFresh = false)
            }
        }
    }

    fun refreshWeather() {
        Log.d("TAG", "in getWeather")
        val location = lastKnownLocation
        if (location != null) {
            fetchAndUpdateUI(location.first, location.second, true)
        }
    }

    fun onLocationPermissionDenied() = viewModelScope.launch {
        Log.d("TAG", "in onLocationPermissionDenied")
        val cachedLocation = locationRepository.getDefaultLocation().firstOrNull()
        if (cachedLocation == null) {
            _weatherException.emit(WeatherException.LocationUnavailable())
            _forecastException.emit(WeatherException.LocationUnavailable())
            _isInitialized.emit(true)
            return@launch
        }
        lastKnownLocation = cachedLocation.lat to cachedLocation.lon
        fetchAndUpdateUI(cachedLocation.lat, cachedLocation.lon, forceFresh = false)
    }

    fun onLocationPermissionAccepted() = viewModelScope.launch {
        Log.d("TAG", "in onLocationPermissionAccepted")
        val resolution = locationManager.checkGpsRequireResolution()
        if (resolution != null) {
            _locationEvents.send(LocationEvent.RequestGpsResolution(resolution))
            return@launch
        }
        saveCurrentLocationAndUpdateUI()
    }

    fun onGpsResolutionFinished() {
        Log.d("TAG", "in onGpsResolutionFinished")
        saveCurrentLocationAndUpdateUI()
    }

    private fun saveCurrentLocationAndUpdateUI() = viewModelScope.launch {
        Log.d("TAG", "in saveCurrentLocationAndUpdateUI")
        when (val result = locationManager.getCurrentLocation()) {
            is LocationManager.LocationResult.Success -> {
                val location = locationManager.buildLocation(
                    result.lat, result.lon, isDefault = true, isCurrent = true
                )
                lastKnownLocation = location.lat to location.lon
                locationRepository.setCurrent(location)

                Log.d("TAG", "saveCurrentLocationAndUpdateUI: ${result.lat} -- ${result.lon}")
            }

            is LocationManager.LocationResult.Failure -> {
                Log.d("TAG", "saveCurrentLocationAndUpdateUI: ${result.exception}")
                lastKnownLocation?.let { fetchAndUpdateUI(it.first, it.second, forceFresh = false) }

                _weatherException.emit(result.exception)
                _forecastException.emit(result.exception)
                _isInitialized.emit(true)
            }
        }
    }

    private fun fetchAndUpdateUI(lat: Double, lon: Double, forceFresh: Boolean) {
        Log.d("TAG", "in fetchAndUpdateUI")
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            launch { fetchCurrentWeather(lat, lon, forceFresh) }
            launch { fetchForecast(lat, lon, forceFresh) }
        }
    }

    private suspend fun fetchCurrentWeather(lat: Double, lon: Double, forceFresh: Boolean) {
        weatherRepository.getCurrentWeather(lat, lon, forceFresh).collect { resource ->
            when (resource) {
                is Resource.Loading -> _weatherLoading.emit(true)
                is Resource.Success -> {
                    Log.d("TAG", "in fetchCurrentWeather ${resource.data}")
                    _currentWeatherState.emit(resource.data)
                    _weatherException.emit(null)
                    _weatherLoading.emit(false)
                    _isInitialized.emit(true)
                }

                is Resource.Failure -> {
                    _weatherException.emit(resource.exception)
                    _weatherLoading.emit(false)
                    _isInitialized.emit(true)
                }
            }
        }
    }

    private suspend fun fetchForecast(lat: Double, lon: Double, forceFresh: Boolean) {
        weatherRepository.getForecast(lat, lon, forceFresh).collect { resource ->
            when (resource) {
                is Resource.Loading -> _forecastLoading.emit(true)
                is Resource.Success -> {
                    _forecastState.emit(resource.data)
                    _forecastException.emit(null)
                    _forecastLoading.emit(false)
                    _isInitialized.emit(true)
                }

                is Resource.Failure -> {
                    _forecastException.emit(resource.exception)
                    _forecastLoading.emit(false)
                    _isInitialized.emit(true)
                }
            }
        }
    }
}