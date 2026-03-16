package iti.mad.dusk.ui.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import iti.mad.dusk.ui.components.ForecastTab
import iti.mad.dusk.ui.components.ForecastToggle
import iti.mad.dusk.ui.util.LocationManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import iti.mad.dusk.ui.presentation.home.component.ErrorBanner
import iti.mad.dusk.ui.presentation.home.component.SectionSkeleton
import iti.mad.dusk.ui.presentation.home.model.HomeUiState
import iti.mad.dusk.ui.presentation.home.model.LocationEvent
import iti.mad.dusk.ui.util.ObserveAsEvents

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    var bannerHeightPx by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.any { it }) homeViewModel.onLocationPermissionAccepted()
        else homeViewModel.onLocationPermissionDenied()
    }

    val gpsResolutionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        homeViewModel.onGpsResolutionFinished()
    }

    ObserveAsEvents(homeViewModel.locationEvents) { event ->
        when (event) {
            is LocationEvent.RequestPermissions ->
                permissionLauncher.launch(LocationManager.permissions)
            is LocationEvent.RequestGpsResolution ->
                gpsResolutionLauncher.launch(
                    IntentSenderRequest.Builder(event.exception.resolution).build()
                )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        when {
            // Not yet attempted any load — show shimmer unconditionally
            !uiState.isInitialized -> {
                HomeScreenShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .verticalScroll(rememberScrollState())
                )
            }

            // Initialized but no data arrived and at least one error — full screen error
            uiState.currentWeather == null &&
                    uiState.forecast == null &&
                    (uiState.weatherException != null || uiState.forecastException != null) -> {
                FullScreenError(
                    message = (uiState.weatherException ?: uiState.forecastException)
                        ?.message ?: "Something went wrong",
                    icon = Icons.Outlined.WifiOff,
                    onRetry = { homeViewModel.refreshWeather() }
                )
            }

            // At least partial data — show data screen
            // per-section failures are shown as banners
            else -> {
                DataScreen(
                    uiState = uiState,
                    onRefresh = { homeViewModel.refreshWeather() },
                    bannerHeightPx = bannerHeightPx
                )
            }
        }

        // Linear progress bar — sits above everything, below banners
        AnimatedVisibility(
            visible = uiState.isRefreshing,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        }

        // Per-section error banners — measured so DataScreen can offset its content
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .onGloballyPositioned { bannerHeightPx = it.size.height }
        ) {
            ErrorBanner(
                message = uiState.weatherException
                    ?.takeIf { uiState.currentWeather != null }?.message,
                onRetry = { homeViewModel.refreshWeather() }
            )
            ErrorBanner(
                message = uiState.forecastException
                    ?.takeIf { uiState.forecast != null }?.message,
                onRetry = { homeViewModel.refreshWeather() }
            )
        }
    }
}

// ── Data screen ───────────────────────────────────────────────────────────────

@Composable
private fun DataScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    bannerHeightPx: Int,
) {
    val bannerHeightDp = with(LocalDensity.current) { bannerHeightPx.toDp() }
    val pullState = rememberPullToRefreshState()
    var selectedTab by rememberSaveable { mutableStateOf(ForecastTab.HOURLY) }

    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = onRefresh,
        state = pullState,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(top = bannerHeightDp)
        ) {
            Spacer(Modifier.height(16.dp))

            when (val w = uiState.currentWeather) {
                null -> SectionSkeleton(
                    height = 260.dp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                else -> CurrentWeatherHeader(weather = w)
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                ForecastToggle(
                    selected = selectedTab,
                    onSelect = { selectedTab = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))

                when (val f = uiState.forecast) {
                    null -> SectionSkeleton(height = 160.dp)
                    else -> AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            val toRight = targetState == ForecastTab.DAILY
                            (slideInHorizontally(tween(260)) { if (toRight) it else -it } +
                                    fadeIn(tween(260))) togetherWith
                                    (slideOutHorizontally(tween(260)) { if (toRight) -it else it } +
                                            fadeOut(tween(200)))
                        },
                        label = "forecastTab"
                    ) { tab ->
                        when (tab) {
                            ForecastTab.HOURLY -> HourlyStrip(items = f.hourly.take(12))
                            ForecastTab.DAILY -> DailyForecastCard(daily = f.daily)
                        }
                    }
                }
            }
        }
    }
}

// ── Full-screen error ─────────────────────────────────────────────────────────

@Composable
private fun FullScreenError(
    message: String,
    icon: ImageVector,
    onRetry: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) { Text("Try again") }
        }
    }
}