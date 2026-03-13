package iti.mad.dusk.ui.components

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
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun RefreshChip(
    timestampMillis: Long,
    modifier: Modifier = Modifier
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Tick every 30 seconds to keep label fresh
    LaunchedEffect(timestampMillis) {
        while (true) {
            delay(30_000L)
            now = System.currentTimeMillis()
        }
    }

    val label = relativeTime(timestampMillis, now)

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

private fun relativeTime(timestampMillis: Long, now: Long): String {
    if (timestampMillis <= 0L) return "just now"
    val diffMs = now - timestampMillis
    val mins   = (diffMs / 60_000).toInt()
    val hours  = mins / 60
    val days   = hours / 24
    return when {
        mins  < 1   -> "just now"
        mins  < 60  -> "$mins min${if (mins == 1) "" else "s"} ago"
        hours < 24  -> "$hours hr${if (hours == 1) "" else "s"} ago"
        else        -> "$days day${if (days == 1) "" else "s"} ago"
    }
}
