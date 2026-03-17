package iti.mad.dusk.ui.presentation.home.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import iti.mad.dusk.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@SuppressLint("LocalContextGetResourceValueCall", "LocalContextResourcesRead")
@Composable
fun RefreshChip(
    timestampMillis: Long, modifier: Modifier = Modifier
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(timestampMillis) {
        while (true) {
            delay(30_000L)
            now = System.currentTimeMillis()
        }
    }

    val context = LocalContext.current
    val label = remember(timestampMillis, now) {
        if (timestampMillis <= 0L) {
            context.getString(R.string.home_refresh_just_now)
        } else {
            val diffMs = now - timestampMillis
            val mins = (diffMs / 60_000).toInt()
            val hours = mins / 60
            val days = hours / 24
            when {
                mins < 1 -> context.getString(R.string.home_refresh_just_now)
                mins < 60 -> context.resources.getQuantityString(
                    R.plurals.home_refresh_minutes_ago, mins, mins
                )

                hours < 24 -> context.resources.getQuantityString(
                    R.plurals.home_refresh_hours_ago, hours, hours
                )

                else -> context.resources.getQuantityString(
                    R.plurals.home_refresh_days_ago, days, days
                )
            }
        }
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}