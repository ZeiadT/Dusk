package iti.mad.dusk.ui.presentation.alert.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import iti.mad.dusk.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import iti.mad.dusk.domain.model.AlertType
import iti.mad.dusk.ui.presentation.alert.model.AlertEvent
import iti.mad.dusk.ui.presentation.alert.model.AlertSheetState
import iti.mad.dusk.ui.presentation.common.TimePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertBottomSheet(
    state: AlertSheetState,
    onEvent: (AlertEvent) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onEvent(AlertEvent.DismissSheet) },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (state.isEditing) stringResource(R.string.alert_sheet_title_edit) else stringResource(R.string.alert_sheet_title_new),
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = stringResource(R.string.alert_sheet_time_window),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TimeBox(
                    label = stringResource(R.string.alert_sheet_from),
                    hour = state.fromHour,
                    minute = state.fromMinute,
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(AlertEvent.ShowPicker(AlertSheetState.TimePicker.FROM)) })
                TimeBox(
                    label = stringResource(R.string.alert_sheet_to),
                    hour = state.toHour,
                    minute = state.toMinute,
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(AlertEvent.ShowPicker(AlertSheetState.TimePicker.TO)) })
            }

            Text(
                text = stringResource(R.string.alert_sheet_type_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AlertTypeChip(
                    label = stringResource(R.string.alert_sheet_type_notification),
                    selected = state.type == AlertType.NOTIFICATION,
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(AlertEvent.SetType(AlertType.NOTIFICATION)) })
                AlertTypeChip(
                    label = stringResource(R.string.alert_sheet_type_alarm),
                    selected = state.type == AlertType.ALARM,
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(AlertEvent.SetType(AlertType.ALARM)) })
            }

            AnimatedVisibility(visible = state.errorMessage != null) {
                state.errorMessage?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                    )
                }
            }

            Button(
                onClick = { onEvent(AlertEvent.SaveAlert) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(stringResource(R.string.alert_sheet_save))
            }
        }
    }

    state.activePicker?.let { picker ->
        val isFrom = picker == AlertSheetState.TimePicker.FROM
        val title = if (isFrom) stringResource(R.string.alert_sheet_from) else stringResource(R.string.alert_sheet_to)
        val timePickerState = rememberTimePickerState(
            initialHour = if (isFrom) state.fromHour else state.toHour,
            initialMinute = if (isFrom) state.fromMinute else state.toMinute,
            is24Hour = true,
        )

        TimePickerDialog(title, { onEvent(AlertEvent.DismissPicker) }, {
            if (isFrom) onEvent(
                AlertEvent.SetFromTime(
                    timePickerState.hour, timePickerState.minute
                )
            )
            else onEvent(
                AlertEvent.SetToTime(
                    timePickerState.hour, timePickerState.minute
                )
            )
        }) { TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
private fun TimeBox(
    label: String,
    hour: Int,
    minute: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "%02d:%02d".format(hour, minute),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun AlertTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
        )
    }
}