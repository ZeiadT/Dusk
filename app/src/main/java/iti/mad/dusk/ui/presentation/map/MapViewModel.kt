package iti.mad.dusk.ui.presentation.map

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchMultipleSelectionCallback
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSuggestionsCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import iti.mad.dusk.domain.repo.LocationRepository
import iti.mad.dusk.ui.presentation.map.model.MapEvent
import iti.mad.dusk.ui.presentation.map.model.MapNavEvent
import iti.mad.dusk.ui.presentation.map.model.MapSearchResult
import iti.mad.dusk.ui.presentation.map.model.MapUiState
import iti.mad.dusk.ui.util.LocationManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository, private val locationManager: LocationManager
) : ViewModel() {

    private val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
        SearchEngineSettings()
    )

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    private val _navEvents = Channel<MapNavEvent>(Channel.BUFFERED)
    val navEvents = _navEvents.receiveAsFlow()

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.OnSelectPoint -> _uiState.update {
                it.copy(selectedPoint = event.point)
            }

            is MapEvent.OnSave -> viewModelScope.launch {
                // TODO: Implement this using event.point
                locationRepository.saveLocation(
                    locationManager.buildLocation(
                        event.point.latitude(), event.point.longitude()
                    )
                )
                _navEvents.send(MapNavEvent.GoBack)
            }

            is MapEvent.OnBack -> viewModelScope.launch {
                _navEvents.send(MapNavEvent.GoBack)
            }

            is MapEvent.OnQueryChanged -> updateQuery(event.query)

            is MapEvent.OnSelectSearchResult -> _uiState.update {
                it.copy(
                    selectedPoint = event.result.point, searchState = it.searchState.copy(
                        query = event.result.name,
                        suggestions = emptyList(),
                        showSuggestions = false,
                    )
                )
            }

            is MapEvent.ClearSearch -> _uiState.update {
                it.copy(
                    searchState = it.searchState.copy(
                        query = "",
                        suggestions = emptyList(),
                        showSuggestions = false,
                    )
                )
            }
        }
    }

    private fun updateQuery(query: String) {
        _uiState.update { it.copy(searchState = it.searchState.copy(query = query)) }

        if (query.length <= 2) {
            _uiState.update {
                it.copy(
                    searchState = it.searchState.copy(
                        suggestions = emptyList(), showSuggestions = false
                    )
                )
            }
            return
        }

        _uiState.update {
            it.copy(searchState = it.searchState.copy(isSearching = true, showSuggestions = true))
        }

        viewModelScope.launch {
            searchEngine.search(
                query = query,
                options = SearchOptions(limit = 5),
                callback = object : SearchSuggestionsCallback {
                    override fun onSuggestions(
                        suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo
                    ) {
                        searchEngine.select(
                            suggestions = suggestions,
                            callback = object : SearchMultipleSelectionCallback {
                                override fun onResult(
                                    suggestions: List<SearchSuggestion>,
                                    results: List<SearchResult>,
                                    responseInfo: ResponseInfo,
                                ) {
                                    _uiState.update {
                                        it.copy(
                                            searchState = it.searchState.copy(
                                                isSearching = false,
                                                suggestions = results.map { result ->
                                                    MapSearchResult(
                                                        name = result.name,
                                                        address = result.address?.formattedAddress(),
                                                        point = result.coordinate,
                                                    )
                                                })
                                        )
                                    }
                                }

                                override fun onError(e: Exception) {
                                    _uiState.update {
                                        it.copy(searchState = it.searchState.copy(isSearching = false))
                                    }
                                }
                            })
                    }

                    override fun onError(e: Exception) {
                        _uiState.update {
                            it.copy(searchState = it.searchState.copy(isSearching = false))
                        }
                    }
                })
        }
    }
}