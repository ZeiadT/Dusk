package iti.mad.dusk.viewmodels

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import iti.mad.dusk.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WeatherSettings
import iti.mad.dusk.domain.model.WindSpeedUnit
import iti.mad.dusk.domain.repo.SettingsRepository
import iti.mad.dusk.ui.presentation.setting.SettingsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
    }

    @Test
    fun `when ViewModel initializes, settings state flow reflects repository data`() = runTest {
        // Arrange
        val expectedSettings = WeatherSettings(
            temperatureUnit = TemperatureUnit.FAHRENHEIT,
            windSpeedUnit = WindSpeedUnit.KILOMETERS_PER_HOUR
        )
        every { repository.getSettings() } returns flowOf(expectedSettings)

        // Act
        viewModel = SettingsViewModel(repository)

        // Assert
        val actualSettings = viewModel.settings.first()
        assertEquals(expectedSettings, actualSettings)
    }

    @Test
    fun `when setTemperatureUnit is called, repository updates temperature unit`() = runTest {
        // Arrange
        viewModel = SettingsViewModel(repository)
        val newUnit = TemperatureUnit.FAHRENHEIT

        // Act
        viewModel.setTemperatureUnit(newUnit)

        // Assert
        coVerify(exactly = 1) { repository.setTemperatureUnit(newUnit) }
    }

    @Test
    fun `when setWindSpeedUnit is called, repository updates wind speed unit`() = runTest {
        // Arrange
        viewModel = SettingsViewModel(repository)
        val newUnit = WindSpeedUnit.KILOMETERS_PER_HOUR

        // Act
        viewModel.setWindSpeedUnit(newUnit)

        // Assert
        coVerify(exactly = 1) { repository.setWindSpeedUnit(newUnit) }
    }
}