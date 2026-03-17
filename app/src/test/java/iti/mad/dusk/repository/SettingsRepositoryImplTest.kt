package iti.mad.dusk.repository

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import iti.mad.dusk.data.local.datasource.SettingsDataStore
import iti.mad.dusk.data.repo.SettingsRepositoryImpl
import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WeatherSettings
import iti.mad.dusk.domain.model.WindSpeedUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsRepositoryImplTest {

    private lateinit var dataStore: SettingsDataStore
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setUp() {
        dataStore = mockk(relaxed = true)
        repository = SettingsRepositoryImpl(dataStore)
    }

    @Test
    fun `when getSettings is called, it returns the flow from the data store`() = runTest {
        // Arrange
        val expectedSettings = WeatherSettings(
            temperatureUnit = TemperatureUnit.CELSIUS,
            windSpeedUnit = WindSpeedUnit.METERS_PER_SECOND
        )
        every { dataStore.settingsFlow } returns flowOf(expectedSettings)

        // Act
        val actualSettings = repository.getSettings().first()

        // Assert
        assertEquals(expectedSettings, actualSettings)
    }

    @Test
    fun `when setTemperatureUnit is called, it delegates exactly to data store`() = runTest {
        // Arrange
        val newUnit = TemperatureUnit.CELSIUS

        // Act
        repository.setTemperatureUnit(newUnit)

        // Assert
        coVerify(exactly = 1) { dataStore.setTemperatureUnit(newUnit) }
    }

    @Test
    fun `when setWindSpeedUnit is called, it delegates exactly to data store`() = runTest {
        // Arrange
        val newUnit = WindSpeedUnit.METERS_PER_SECOND

        // Act
        repository.setWindSpeedUnit(newUnit)

        // Assert
        coVerify(exactly = 1) { dataStore.setWindSpeedUnit(newUnit) }
    }
}