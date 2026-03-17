package iti.mad.dusk.ui.presentation.home.component

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
import androidx.compose.ui.res.stringResource
import iti.mad.dusk.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iti.mad.dusk.core.util.UnitConverter
import iti.mad.dusk.domain.model.CurrentWeather
import iti.mad.dusk.domain.model.WeatherSettings
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherHeader(
    modifier: Modifier = Modifier,
    weather: CurrentWeather,
    settings: WeatherSettings,
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
                    text  = stringResource(R.string.home_location_format, weather.cityName, weather.country),
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
                text       = stringResource(R.string.home_degree_symbol),
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
            Text(stringResource(R.string.home_hi_label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Text(
                stringResource(R.string.home_temp_format, weather.temperature.max.roundToInt()),
                color      = MaterialTheme.colorScheme.primary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(stringResource(R.string.home_hi_lo_separator), color = MaterialTheme.colorScheme.outline, fontSize = 13.sp)
            Text(stringResource(R.string.home_lo_label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Text(
                stringResource(R.string.home_temp_format, weather.temperature.min.roundToInt()),
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
                label = stringResource(R.string.home_stat_feels_like),
                value = stringResource(R.string.home_temp_format, weather.temperature.feelsLike.roundToInt())
            )
            WeatherStatItem(
                icon  = Icons.Outlined.WaterDrop,
                label = stringResource(R.string.home_stat_humidity),
                value = stringResource(R.string.home_percent_format, weather.temperature.humidity)
            )
            WeatherStatItem(
                icon  = Icons.Outlined.Air,
                label = stringResource(R.string.home_stat_wind),
                value = stringResource(R.string.home_wind_format, weather.wind.speed.roundToInt(), UnitConverter.windSpeedSymbol(settings.windSpeedUnit))
            )
            WeatherStatItem(
                icon  = Icons.Outlined.Compress,
                label = stringResource(R.string.home_stat_pressure),
                value = stringResource(R.string.home_pressure_format, weather.temperature.pressure)
            )
            WeatherStatItem(
                icon  = Icons.Outlined.Visibility,
                label = stringResource(R.string.home_stat_visibility),
                value = stringResource(R.string.home_percent_format, weather.cloudsInPercentage)
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