package iti.mad.dusk.ui.presentation.alert.model

sealed interface SnackbarEvent {
    data class Success(val message: String) : SnackbarEvent
    data class Error(val message: String) : SnackbarEvent
}