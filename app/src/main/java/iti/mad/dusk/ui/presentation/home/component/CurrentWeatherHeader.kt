package iti.mad.dusk.ui.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.ui.components.RefreshChip
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherHeader(
    weather: CurrentWeather,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // ── Location row + refresh chip ───────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text  = "${weather.cityName}, ${weather.country}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            RefreshChip(timestampMillis = weather.fetchTimestampMillis)
        }

        Spacer(Modifier.height(16.dp))

        // ── Temperature + condition ───────────────────────────────────────
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text       = weather.temperature.current.roundToInt().toString(),
                fontSize   = 88.sp,
                fontWeight = FontWeight.Thin,
                color      = MaterialTheme.colorScheme.onBackground,
                lineHeight = 88.sp
            )
            Text(
                text       = "°",
                fontSize   = 44.sp,
                fontWeight = FontWeight.Light,
                color      = MaterialTheme.colorScheme.primary,
                modifier   = Modifier.padding(top = 12.dp)
            )
        }

        Text(
            text  = weather.condition.description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Light
        )

        Spacer(Modifier.height(4.dp))

        // Hi / Lo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Thermostat,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("H: ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Text(
                "${weather.temperature.max.roundToInt()}°",
                color      = MaterialTheme.colorScheme.primary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text("  ·  ", color = MaterialTheme.colorScheme.outline, fontSize = 13.sp)
            Text("L: ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Text(
                "${weather.temperature.min.roundToInt()}°",
                color      = MaterialTheme.colorScheme.secondary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(20.dp))

        // ── Stats grid ────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherStatItem(
                icon  = Icons.Outlined.Thermostat,
                label = "Feels like",
                value = "${weather.temperature.feelsLike.roundToInt()}°"
            )
            WeatherStatItem(
                icon  = Icons.Outlined.WaterDrop,
                label = "Humidity",
                value = "${weather.temperature.humidity}%"
            )
            WeatherStatItem(
                icon  = Icons.Outlined.Air,
                label = "Wind",
                value = "${weather.wind.speed.roundToInt()} m/s"
            )
            WeatherStatItem(
                icon  = Icons.Outlined.Compress,
                label = "Pressure",
                value = "${weather.temperature.pressure} hPa"
            )
            WeatherStatItem(
                icon  = Icons.Outlined.Visibility,
                label = "Visibility",
                value = "${weather.cloudsInPercentage}%"
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun WeatherStatItem(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint     = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            value,
            style      = MaterialTheme.typography.bodyMedium,
            color      = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
