package iti.mad.dusk.ui.presentation.map.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
fun MapView(
    selectedPoint: Point?,
    mapViewportState: MapViewportState,
    onPointSelected: (Point) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    MapboxMap(
        modifier = modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        logo = {},
        attribution = {},
        scaleBar = {},
        onMapClickListener = onPointSelected,
    ) {
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck()
                enabled = true
                pulsingEnabled = true
                puckBearingEnabled = false
            }
            mapViewportState.transitionToFollowPuckState()
        }

        selectedPoint?.let { point ->
            CircleAnnotation(point = point) {
                circleRadius = 12.0
                circleColor = primaryColor
                circleStrokeWidth = 3.0
                circleStrokeColor = Color.White
                circleOpacity = 0.9
            }
        }
    }
}