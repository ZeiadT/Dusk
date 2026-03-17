package iti.mad.dusk.ui.presentation.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iti.mad.dusk.domain.model.WeatherAlert
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import iti.mad.dusk.R
import iti.mad.dusk.domain.repo.AlertRepository
import iti.mad.dusk.ui.presentation.alert.model.AlertEvent
import iti.mad.dusk.ui.presentation.alert.model.AlertSheetState
import iti.mad.dusk.ui.presentation.alert.model.AlertUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alertRepository: AlertRepository,
) : ViewModel() {

    val uiState: StateFlow<AlertUiState> = alertRepository.getAllAlerts()
        .map { alerts ->
            AlertUiState(
                activeAlerts = alerts.filter { it.isActive },
                inactiveAlerts = alerts.filter { !it.isActive },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AlertUiState()
        )

    private val _sheetState = MutableStateFlow(AlertSheetState())
    val sheetState: StateFlow<AlertSheetState> = _sheetState

    fun onEvent(event: AlertEvent) {
        when (event) {
            is AlertEvent.OpenAddSheet -> _sheetState.update {
                AlertSheetState(isVisible = true)
            }

            is AlertEvent.OpenEditSheet -> _sheetState.update {
                AlertSheetState(
                    isVisible = true,
                    editingId = event.alert.id,
                    fromHour = event.alert.fromHour,
                    fromMinute = event.alert.fromMinute,
                    toHour = event.alert.toHour,
                    toMinute = event.alert.toMinute,
                    type = event.alert.type,
                )
            }

            is AlertEvent.DismissSheet -> _sheetState.update { AlertSheetState() }

            is AlertEvent.SetFromTime -> _sheetState.update {
                it.copy(fromHour = event.hour, fromMinute = event.minute, activePicker = null, errorMessage = null)
            }

            is AlertEvent.SetToTime -> _sheetState.update {
                it.copy(toHour = event.hour, toMinute = event.minute, activePicker = null, errorMessage = null)
            }

            is AlertEvent.SetType -> _sheetState.update {
                it.copy(type = event.type)
            }

            is AlertEvent.ShowPicker -> _sheetState.update {
                it.copy(activePicker = event.picker)
            }

            is AlertEvent.DismissPicker -> _sheetState.update {
                it.copy(activePicker = null)
            }

            is AlertEvent.SaveAlert -> viewModelScope.launch {
                val sheet = _sheetState.value
                val fromMinutes = sheet.fromHour * 60 + sheet.fromMinute
                val toMinutes = sheet.toHour * 60 + sheet.toMinute

                if (fromMinutes >= toMinutes) {
                    _sheetState.update { it.copy(errorMessage = context.getString(R.string.alert_error_from_before_to)) }
                    return@launch
                }

                val allAlerts = uiState.value.activeAlerts + uiState.value.inactiveAlerts
                val overlapping = allAlerts.any { existing ->
                    val isSelf = existing.id == sheet.editingId
                    if (isSelf) return@any false
                    val existingFrom = existing.fromHour * 60 + existing.fromMinute
                    val existingTo = existing.toHour * 60 + existing.toMinute
                    fromMinutes < existingTo && existingFrom < toMinutes
                }

                if (overlapping) {
                    _sheetState.update { it.copy(errorMessage = context.getString(R.string.alert_error_overlap)) }
                    return@launch
                }

                val alert = WeatherAlert(
                    id = sheet.editingId ?: 0,
                    fromHour = sheet.fromHour,
                    fromMinute = sheet.fromMinute,
                    toHour = sheet.toHour,
                    toMinute = sheet.toMinute,
                    type = sheet.type,
                    isActive = true,
                )
                if (sheet.isEditing) alertRepository.updateAlert(alert)
                else alertRepository.insertAlert(alert)
                _sheetState.update { AlertSheetState() }
            }

            is AlertEvent.ToggleAlert -> viewModelScope.launch {
                alertRepository.setAlertActive(event.id, event.active)
            }

            is AlertEvent.DeleteAlert -> viewModelScope.launch {
                alertRepository.deleteAlert(event.id)
            }
        }
    }
}