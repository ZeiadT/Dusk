package iti.mad.dusk.ui.presentation.locations

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import iti.mad.dusk.R
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import iti.mad.dusk.ui.presentation.locations.component.LocationCard
import iti.mad.dusk.ui.presentation.locations.model.SnackBarEvent
import iti.mad.dusk.ui.presentation.main.FabState
import iti.mad.dusk.ui.presentation.main.LocalSnackbarHostState
import iti.mad.dusk.ui.presentation.main.MainViewModel
import androidx.compose.ui.platform.LocalContext
import iti.mad.dusk.ui.util.ObserveAsEvents

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun LocationsScreen(
    modifier: Modifier = Modifier,
    navigateToMap: () -> Unit,
) {
    val locationsViewModel: LocationsViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = hiltViewModel(LocalActivity.current as ViewModelStoreOwner)

    val locations by locationsViewModel.locationsState.collectAsStateWithLifecycle()
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mainViewModel.setFab(
            FabState(
                icon = Icons.Outlined.Add,
                label = context.getString(R.string.locations_fab_add),
                onClick = navigateToMap
            )
        )
        mainViewModel.setBottomBar(true)
    }

    ObserveAsEvents(locationsViewModel.events) { event ->
        when (event) {
            is SnackBarEvent.LocationRemoved -> snackbarHostState.showSnackbar(
                message = context.getString(R.string.locations_snack_removed),
                duration = SnackbarDuration.Short
            )

            is SnackBarEvent.LocationAlreadyDefault -> snackbarHostState.showSnackbar(
                message = context.getString(R.string.locations_snack_already_default),
                duration = SnackbarDuration.Short
            )

            is SnackBarEvent.DeleteDefaultNotSupported -> snackbarHostState.showSnackbar(
                message = context.getString(R.string.locations_snack_cant_delete_default),
                duration = SnackbarDuration.Short
            )

            is SnackBarEvent.DeleteCurrentNotSupported -> snackbarHostState.showSnackbar(
                message = context.getString(R.string.locations_snack_cant_delete_current),
                duration = SnackbarDuration.Short
            )
        }
    }

    if (locations.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.locations_empty))
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }
            items(
                items = locations, key = { it.lat.toString() + it.lon.toString() }) { location ->
                LocationCard(
                    cityName = location.cityName,
                    country = location.country,
                    isCurrent = location.isCurrent,
                    isDefault = location.isDefault,
                    onSetDefault = {
                        locationsViewModel.setDefaultLocation(location.lat, location.lon)
                    },
                    onDelete = {
                        locationsViewModel.deleteLocation(location.lat, location.lon)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}