package iti.mad.dusk.ui.presentation.locations.model

sealed interface SnackBarEvent {
    data object LocationRemoved : SnackBarEvent
    data object LocationAlreadyDefault : SnackBarEvent
    data object DeleteDefaultNotSupported : SnackBarEvent
    data object DeleteCurrentNotSupported : SnackBarEvent
}