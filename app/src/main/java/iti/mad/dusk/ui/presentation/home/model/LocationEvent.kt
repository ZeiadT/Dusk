package iti.mad.dusk.ui.presentation.home.model

import com.google.android.gms.common.api.ResolvableApiException


sealed interface LocationEvent {
    data object RequestPermissions : LocationEvent

    data class RequestGpsResolution(val exception: ResolvableApiException) : LocationEvent
}
