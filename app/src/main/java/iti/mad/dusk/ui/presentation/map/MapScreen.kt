package iti.mad.dusk.ui.presentation.map

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import iti.mad.dusk.ui.presentation.main.MainViewModel
import iti.mad.dusk.ui.presentation.map.component.MapSearchBar
import iti.mad.dusk.ui.presentation.map.component.MapView
import iti.mad.dusk.ui.presentation.map.model.MapEvent
import iti.mad.dusk.ui.presentation.map.model.MapNavEvent
import iti.mad.dusk.ui.util.ObserveAsEvents

@Composable
fun MapScreen(
    modifier: Modifier = Modifier, navigateBack: () -> Unit
) {

    val mapViewModel: MapViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = hiltViewModel(LocalActivity.current as ViewModelStoreOwner)

    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    val mapViewportState = rememberMapViewportState()

    LaunchedEffect(Unit) { mainViewModel.setBottomBar(false) }
    DisposableEffect(Unit) { onDispose { mainViewModel.setBottomBar(true) } }

    ObserveAsEvents(mapViewModel.navEvents) { event ->
        when (event) {
            is MapNavEvent.GoBack -> navigateBack()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapView(
            selectedPoint = uiState.selectedPoint,
            mapViewportState = mapViewportState,
            onPointSelected = { point ->
                mapViewModel.onEvent(MapEvent.OnSelectPoint(point))
                true
            },
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 0.dp,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                ) {
                    IconButton(onClick = { mapViewModel.onEvent(MapEvent.OnBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }

                MapSearchBar(
                    searchState = uiState.searchState,
                    onEvent = mapViewModel::onEvent,
                    onResultSelected = { result ->
                        mapViewportState.flyTo(cameraOptions = cameraOptions {
                            center(result.point)
                            zoom(14.0)
                            pitch(0.0)
                        }, animationOptions = MapAnimationOptions.mapAnimationOptions {
                            duration(800)
                        })
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Save button — appears when a point is selected
        AnimatedVisibility(
            visible = uiState.selectedPoint != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 36.dp)
        ) {
            Button(
                onClick = {
                    uiState.selectedPoint?.let { mapViewModel.onEvent(MapEvent.OnSave(it)) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Save location")
            }
        }
    }
}
