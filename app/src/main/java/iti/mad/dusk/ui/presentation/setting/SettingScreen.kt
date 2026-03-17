package iti.mad.dusk.ui.presentation.setting

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import iti.mad.dusk.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import iti.mad.dusk.app.MainActivity
import iti.mad.dusk.domain.model.Language
import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WindSpeedUnit
import iti.mad.dusk.ui.presentation.main.MainViewModel

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = hiltViewModel(LocalActivity.current as ViewModelStoreOwner)
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    // ✅ get activity to restart it when locale changes
    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        mainViewModel.setFab(null)
        mainViewModel.setBottomBar(true)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        // ── Units section ────────────────────────────────────────────
        item {
            SettingsSectionHeader(title = stringResource(R.string.settings_section_units))
        }

        item {
            SettingsOptionGroup(
                label = stringResource(R.string.settings_label_temperature),
                options = TemperatureUnit.entries,
                selected = settings.temperatureUnit,
                labelFor = {
                    if (it == TemperatureUnit.CELSIUS)
                        stringResource(R.string.settings_temp_celsius)
                    else
                        stringResource(R.string.settings_temp_fahrenheit)
                },
                onSelect = viewModel::setTemperatureUnit,
            )
        }

        item { HorizontalDivider() }

        item {
            SettingsOptionGroup(
                label = stringResource(R.string.settings_label_wind_speed),
                options = WindSpeedUnit.entries,
                selected = settings.windSpeedUnit,
                labelFor = {
                    if (it == WindSpeedUnit.METERS_PER_SECOND)
                        stringResource(R.string.settings_wind_ms)
                    else
                        stringResource(R.string.settings_wind_kmh)
                },
                onSelect = viewModel::setWindSpeedUnit,
            )
        }

        item { HorizontalDivider() }

        // ── Language section ─────────────────────────────────────────
        item {
            SettingsSectionHeader(title = stringResource(R.string.settings_section_language))
        }

        item {
            SettingsOptionGroup(
                label = stringResource(R.string.settings_label_language),
                options = Language.entries,
                selected = settings.language,
                labelFor = {
                    when (it) {
                        Language.ENGLISH -> stringResource(R.string.settings_language_english)
                        Language.ARABIC  -> stringResource(R.string.settings_language_arabic)
                    }
                },
                onSelect = { language ->
                    viewModel.setLanguage(language)
                    (activity as? MainActivity)?.applyLocale(language)
//                    activity?.recreate()
                },
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun <T> SettingsOptionGroup(
    label: String,
    options: List<T>,
    selected: T,
    labelFor: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = labelFor(option),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                RadioButton(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                )
            }
        }
    }
}