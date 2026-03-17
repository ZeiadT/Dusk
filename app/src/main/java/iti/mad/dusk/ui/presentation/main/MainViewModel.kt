package iti.mad.dusk.ui.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iti.mad.dusk.app.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _fabState = MutableStateFlow<FabState?>(null)
    private val _bottomBarState = MutableStateFlow(true)

    val scaffoldState: StateFlow<DuskScaffoldState> =
        combine(_fabState, _bottomBarState) { fab, bottomBar ->
            DuskScaffoldState(fab, bottomBar)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DuskScaffoldState()
        )

    fun setFab(state: FabState?) {
        _fabState.value = state
    }

    fun setBottomBar(show: Boolean) {
        _bottomBarState.value = show
    }

    fun onHomeNavigation(){
        setFab(null)
    }
}