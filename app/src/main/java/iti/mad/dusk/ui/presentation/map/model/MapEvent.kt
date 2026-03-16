package iti.mad.dusk.ui.presentation.map.model

import com.mapbox.geojson.Point

sealed interface MapEvent {
    data object OnBack : MapEvent
    data class OnSelectPoint(val point: Point) : MapEvent
    data class OnSave(val point: Point) : MapEvent
    data class OnQueryChanged(val query: String) : MapEvent
    data class OnSelectSearchResult(val result: MapSearchResult) : MapEvent
    data object ClearSearch : MapEvent
}

sealed interface MapNavEvent {
    data object GoBack : MapNavEvent
}
