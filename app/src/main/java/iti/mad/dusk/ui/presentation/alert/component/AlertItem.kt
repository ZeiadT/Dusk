package iti.mad.dusk.ui.presentation.alert.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import iti.mad.dusk.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import iti.mad.dusk.domain.model.AlertType
import iti.mad.dusk.domain.model.WeatherAlert
import iti.mad.dusk.ui.presentation.alert.model.AlertEvent

@Composable
fun AlertItem(
    alert: WeatherAlert,
    onEvent: (AlertEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (alert.isActive) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (alert.isActive) Icons.Outlined.NotificationsActive
                    else Icons.Outlined.NotificationsOff,
                    contentDescription = null,
                    tint = if (alert.isActive) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "%02d:%02d – %02d:%02d".format(
                        alert.fromHour, alert.fromMinute,
                        alert.toHour, alert.toMinute,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    AlertBadge(
                        label = if (alert.type == AlertType.NOTIFICATION) stringResource(R.string.alert_badge_notification) else stringResource(R.string.alert_badge_alarm),
                        containerColor = if (alert.type == AlertType.NOTIFICATION)
                            MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = if (alert.type == AlertType.NOTIFICATION)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    AlertBadge(
                        label = if (alert.isActive) stringResource(R.string.alert_badge_active) else stringResource(R.string.alert_badge_inactive),
                        containerColor = if (alert.isActive)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (alert.isActive)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = stringResource(R.string.alert_menu_options))
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(if (alert.isActive) stringResource(R.string.alert_menu_deactivate) else stringResource(R.string.alert_menu_activate)) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (alert.isActive) Icons.Outlined.NotificationsOff
                                else Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onEvent(AlertEvent.ToggleAlert(alert.id, !alert.isActive))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.alert_menu_update)) },
                        leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onEvent(AlertEvent.OpenEditSheet(alert))
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.alert_menu_delete), color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onEvent(AlertEvent.DeleteAlert(alert.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertBadge(
    label: String,
    containerColor: Color,
    contentColor: Color,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}