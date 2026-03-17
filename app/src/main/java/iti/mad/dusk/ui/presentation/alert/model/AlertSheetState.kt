package iti.mad.dusk.ui.presentation.alert.model

import iti.mad.dusk.domain.model.AlertType

data class AlertSheetState(
    val isVisible: Boolean = false,
    val editingId: Long? = null,
    val fromHour: Int = 8,
    val fromMinute: Int = 0,
    val toHour: Int = 9,
    val toMinute: Int = 0,
    val type: AlertType = AlertType.NOTIFICATION,
    val activePicker: TimePicker? = null,
    val errorMessage: String? = null,
) {
    val isEditing get() = editingId != null

    enum class TimePicker { FROM, TO }
}