package iti.mad.dusk.ui.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iti.mad.dusk.domain.model.ForecastItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HourlyStrip(
    items: List<ForecastItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp)
    ) {
        Text(
            "HOURLY",
            style    = MaterialTheme.typography.labelSmall,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding        = PaddingValues(horizontal = 16.dp)
        ) {
            items(items) { item -> HourlyItemCell(item) }
        }
    }
}

@Composable
private fun HourlyItemCell(item: ForecastItem) {
    val timeLabel = try {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(item.timestamp * 1000))
    } catch (e: Exception) {
        item.dateText.takeLast(5)
    }

    val isDay    = item.isDay
    val accent   = if (isDay) MaterialTheme.colorScheme.primary
                   else MaterialTheme.colorScheme.secondary
    val bgColor  = accent.copy(alpha = 0.12f)

    Column(
        modifier = Modifier
            .width(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            timeLabel,
            style     = MaterialTheme.typography.labelSmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize  = 10.sp
        )
        Text(
            "${item.temperature.current.roundToInt()}°",
            color      = accent,
            fontSize   = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (item.precipitationProbability > 0.1f) {
            Text(
                "${(item.precipitationProbability * 100).roundToInt()}%",
                color    = MaterialTheme.colorScheme.secondary,
                fontSize = 9.sp
            )
        }
    }
}
