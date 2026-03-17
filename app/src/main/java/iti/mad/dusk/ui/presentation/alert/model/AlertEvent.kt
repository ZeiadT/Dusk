package iti.mad.dusk.ui.presentation.alert.model

import iti.mad.dusk.domain.model.AlertType
import iti.mad.dusk.domain.model.WeatherAlert

sealed interface AlertEvent {
    data object OpenAddSheet : AlertEvent
    data class OpenEditSheet(val alert: WeatherAlert) : AlertEvent
    data object DismissSheet : AlertEvent

    data class SetFromTime(val hour: Int, val minute: Int) : AlertEvent
    data class SetToTime(val hour: Int, val minute: Int) : AlertEvent
    data class SetType(val type: AlertType) : AlertEvent
    data class ShowPicker(val picker: AlertSheetState.TimePicker) : AlertEvent
    data object DismissPicker : AlertEvent

    data object SaveAlert : AlertEvent
    data class ToggleAlert(val id: Long, val active: Boolean) : AlertEvent
    data class DeleteAlert(val id: Long) : AlertEvent
}
