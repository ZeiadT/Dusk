package iti.mad.dusk.ui.presentation.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iti.mad.dusk.domain.repo.LocationRepository
import iti.mad.dusk.ui.presentation.locations.model.SnackBarEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _events = Channel<SnackBarEvent>()
    val events = _events.receiveAsFlow()

    val locationsState = locationRepository.getAllLocations().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    fun setDefaultLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            if (locationRepository.isDefault(lat, lon)) {
                _events.send(SnackBarEvent.LocationAlreadyDefault)
                return@launch
            }

            locationRepository.setDefaultLocation(lat, lon)
        }
    }

    fun deleteLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            if (locationRepository.isDefault(lat, lon)) {
                _events.send(SnackBarEvent.DeleteDefaultNotSupported)
                return@launch
            }

            if (locationRepository.isCurrent(lat, lon)) {
                _events.send(SnackBarEvent.DeleteCurrentNotSupported)
                return@launch
            }

            locationRepository.deleteLocation(lat, lon)
            _events.send(SnackBarEvent.LocationRemoved)
        }
    }
}