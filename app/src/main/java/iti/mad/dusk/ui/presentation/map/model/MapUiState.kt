package iti.mad.dusk.ui.presentation.map.model

import com.mapbox.geojson.Point

data class MapSearchResult(
    val name: String,
    val address: String?,
    val point: Point,
)

data class MapSearchState(
    val query: String = "",
    val suggestions: List<MapSearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val showSuggestions: Boolean = false,
)

data class MapUiState(
    val selectedPoint: Point? = null,
    val searchState: MapSearchState = MapSearchState(),
)
