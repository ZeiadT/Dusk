package iti.mad.dusk.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import iti.mad.dusk.data.local.datasource.SettingsDataStore
import iti.mad.dusk.domain.model.TemperatureUnit
import iti.mad.dusk.domain.model.WeatherSettings
import iti.mad.dusk.domain.model.WindSpeedUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsDataStoreTest {

    // use UnconfinedTestDispatcher so flows emit immediately
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var testFile: File

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        testFile = File(context.filesDir, "test_settings_${System.currentTimeMillis()}.preferences_pb")

        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testFile }
        )

        settingsDataStore = SettingsDataStore(testDataStore)
    }

    @After
    fun tearDown() {
        testFile.deleteRecursively()
    }

    // ── settingsFlow ──────────────────────────────────────────────────────────

    @Test
    fun settingsFlow_emitsDefaults_whenNoValuesSaved() = testScope.runTest {
        val settings = settingsDataStore.settingsFlow.first()

        assertEquals(WeatherSettings(), settings)
        assertEquals(TemperatureUnit.CELSIUS, settings.temperatureUnit)
        assertEquals(WindSpeedUnit.METERS_PER_SECOND, settings.windSpeedUnit)
    }

    @Test
    fun settingsFlow_emitsUpdatedTemperatureUnit_afterSet() = testScope.runTest {
        settingsDataStore.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)

        val settings = settingsDataStore.settingsFlow.first()

        assertEquals(TemperatureUnit.FAHRENHEIT, settings.temperatureUnit)
    }

    @Test
    fun settingsFlow_emitsUpdatedWindSpeedUnit_afterSet() = testScope.runTest {
        settingsDataStore.setWindSpeedUnit(WindSpeedUnit.KILOMETERS_PER_HOUR)

        val settings = settingsDataStore.settingsFlow.first()

        assertEquals(WindSpeedUnit.KILOMETERS_PER_HOUR, settings.windSpeedUnit)
    }

    // ── setTemperatureUnit ────────────────────────────────────────────────────

    @Test
    fun setTemperatureUnit_toCelsius_persistsCorrectly() = testScope.runTest {
        // set to fahrenheit first then switch back
        settingsDataStore.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        settingsDataStore.setTemperatureUnit(TemperatureUnit.CELSIUS)

        val settings = settingsDataStore.settingsFlow.first()

        assertEquals(TemperatureUnit.CELSIUS, settings.temperatureUnit)
    }

    @Test
    fun setTemperatureUnit_doesNotAffectWindSpeedUnit() = testScope.runTest {
        settingsDataStore.setWindSpeedUnit(WindSpeedUnit.KILOMETERS_PER_HOUR)
        settingsDataStore.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)

        val settings = settingsDataStore.settingsFlow.first()

        // wind speed must remain unchanged
        assertEquals(WindSpeedUnit.KILOMETERS_PER_HOUR, settings.windSpeedUnit)
        assertEquals(TemperatureUnit.FAHRENHEIT, settings.temperatureUnit)
    }

    // ── setWindSpeedUnit ──────────────────────────────────────────────────────

    @Test
    fun setWindSpeedUnit_toMetersPerSecond_persistsCorrectly() = testScope.runTest {
        settingsDataStore.setWindSpeedUnit(WindSpeedUnit.KILOMETERS_PER_HOUR)
        settingsDataStore.setWindSpeedUnit(WindSpeedUnit.METERS_PER_SECOND)

        val settings = settingsDataStore.settingsFlow.first()

        assertEquals(WindSpeedUnit.METERS_PER_SECOND, settings.windSpeedUnit)
    }

    @Test
    fun setWindSpeedUnit_doesNotAffectTemperatureUnit() = testScope.runTest {
        settingsDataStore.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        settingsDataStore.setWindSpeedUnit(WindSpeedUnit.KILOMETERS_PER_HOUR)

        val settings = settingsDataStore.settingsFlow.first()

        // temperature must remain unchanged
        assertEquals(TemperatureUnit.FAHRENHEIT, settings.temperatureUnit)
        assertEquals(WindSpeedUnit.KILOMETERS_PER_HOUR, settings.windSpeedUnit)
    }

    // ── both units together ───────────────────────────────────────────────────

    @Test
    fun settingsFlow_reflectsBothUnits_whenBothAreSet() = testScope.runTest {
        settingsDataStore.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
        settingsDataStore.setWindSpeedUnit(WindSpeedUnit.KILOMETERS_PER_HOUR)

        val settings = settingsDataStore.settingsFlow.first()

        assertEquals(
            WeatherSettings(
                temperatureUnit = TemperatureUnit.FAHRENHEIT,
                windSpeedUnit = WindSpeedUnit.KILOMETERS_PER_HOUR,
            ),
            settings
        )
    }
}