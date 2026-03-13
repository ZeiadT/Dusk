package iti.mad.dusk.ui.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iti.mad.dusk.domain.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DailyForecastCard(
    daily: List<DailyForecast>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Text(
            "5-DAY FORECAST",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        daily.forEachIndexed { index, day ->
            DailyRow(day = day)
            if (index < daily.lastIndex) {
                HorizontalDivider(
                    modifier  = Modifier.padding(vertical = 10.dp),
                    color     = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
private fun DailyRow(day: DailyForecast) {
    val dayLabel = try {
        val inFmt  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outFmt = SimpleDateFormat("EEE", Locale.getDefault())
        outFmt.format(inFmt.parse(day.date)!!).uppercase()
    } catch (e: Exception) {
        day.date.take(3).uppercase()
    }

    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            dayLabel,
            style      = MaterialTheme.typography.bodyMedium,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.width(44.dp)
        )
        Text(
            day.condition.main,
            style    = MaterialTheme.typography.bodySmall,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (day.precipitationProbability > 0.1f) {
            Text(
                "${(day.precipitationProbability * 100).roundToInt()}%",
                color    = MaterialTheme.colorScheme.secondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
        Text(
            "${day.tempMin.roundToInt()}°",
            color      = MaterialTheme.colorScheme.secondary,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.width(32.dp),
            textAlign  = TextAlign.End
        )
        Text(
            " / ",
            color    = MaterialTheme.colorScheme.outlineVariant,
            fontSize = 13.sp
        )
        Text(
            "${day.tempMax.roundToInt()}°",
            color      = MaterialTheme.colorScheme.primary,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier.width(32.dp)
        )
    }
}
