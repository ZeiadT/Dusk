package iti.mad.dusk.ui.presentation.alert

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import iti.mad.dusk.R
import androidx.compose.runtime.DisposableEffect
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import iti.mad.dusk.ui.presentation.alert.component.AlertBottomSheet
import iti.mad.dusk.ui.presentation.alert.component.AlertItem
import iti.mad.dusk.ui.presentation.alert.model.AlertEvent
import iti.mad.dusk.ui.presentation.main.FabState
import iti.mad.dusk.ui.presentation.main.MainViewModel

@SuppressLint("NewApi", "LocalContextGetResourceValueCall")
@Composable
fun AlertScreen(
    modifier: Modifier = Modifier,
    alertViewModel: AlertViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(LocalActivity.current as ViewModelStoreOwner),
) {
    val uiState by alertViewModel.uiState.collectAsStateWithLifecycle()
    val sheetState by alertViewModel.sheetState.collectAsStateWithLifecycle()
    val context = LocalContext.current


    val exactAlarmLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (alarmManager.canScheduleExactAlarms()) {
            alertViewModel.onEvent(AlertEvent.OpenAddSheet)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) return@rememberLauncherForActivityResult

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                exactAlarmLauncher.launch(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                )
                return@rememberLauncherForActivityResult
            }
        }
        alertViewModel.onEvent(AlertEvent.OpenAddSheet)
    }

    val onFabClick = {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val alarmManager = context.getSystemService(AlarmManager::class.java)
                if (!alarmManager.canScheduleExactAlarms()) {
                    exactAlarmLauncher.launch(
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    )
                } else {
                    alertViewModel.onEvent(AlertEvent.OpenAddSheet)
                }
            }
            else -> alertViewModel.onEvent(AlertEvent.OpenAddSheet)
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.setFab(
            FabState(
                icon = Icons.Outlined.Add,
                label = context.getString(R.string.alert_fab_add),
                onClick = onFabClick
            )
        )
    }

    if (uiState.activeAlerts.isEmpty() && uiState.inactiveAlerts.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.alert_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (uiState.activeAlerts.isNotEmpty()) {
                item { SectionHeader(title = stringResource(R.string.alert_section_active)) }
                items(uiState.activeAlerts, key = { it.id }) { alert ->
                    AlertItem(
                        alert = alert,
                        onEvent = alertViewModel::onEvent,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (uiState.inactiveAlerts.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    SectionHeader(title = stringResource(R.string.alert_section_inactive))
                }
                items(uiState.inactiveAlerts, key = { it.id }) { alert ->
                    AlertItem(
                        alert = alert,
                        onEvent = alertViewModel::onEvent,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (sheetState.isVisible) {
        AlertBottomSheet(
            state = sheetState,
            onEvent = alertViewModel::onEvent,
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp),
        )
        HorizontalDivider()
        Spacer(Modifier.height(4.dp))
    }
}