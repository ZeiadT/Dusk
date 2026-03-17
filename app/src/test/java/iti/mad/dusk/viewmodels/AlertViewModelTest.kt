package iti.mad.dusk.viewmodels

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import iti.mad.dusk.MainDispatcherRule
import iti.mad.dusk.domain.model.AlertType
import iti.mad.dusk.domain.model.WeatherAlert
import iti.mad.dusk.domain.repo.AlertRepository
import iti.mad.dusk.ui.presentation.alert.AlertViewModel
import iti.mad.dusk.ui.presentation.alert.model.AlertEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: AlertRepository
    private lateinit var viewModel: AlertViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
    }

    @Test
    fun `when viewmodel initializes, ui state correctly partitions active and inactive alerts`() = runTest {
        // Arrange
        val activeAlert = WeatherAlert(id = 1, fromHour = 8, fromMinute = 0, toHour = 9, toMinute = 0, type = AlertType.NOTIFICATION, isActive = true)
        val inactiveAlert = WeatherAlert(id = 2, fromHour = 10, fromMinute = 0, toHour = 11, toMinute = 0, type = AlertType.ALARM, isActive = false)

        every { repository.getAllAlerts() } returns flowOf(listOf(activeAlert, inactiveAlert))

        // Act
        viewModel = AlertViewModel(repository)
        val actualState = viewModel.uiState.first()

        // Assert
        assertEquals(1, actualState.activeAlerts.size)
        assertEquals(1L, actualState.activeAlerts.first().id)

        assertEquals(1, actualState.inactiveAlerts.size)
        assertEquals(2L, actualState.inactiveAlerts.first().id)
    }

    @Test
    fun `when OpenAddSheet event is triggered, sheet state becomes visible with default values`() = runTest {
        // Arrange
        every { repository.getAllAlerts() } returns flowOf(emptyList())
        viewModel = AlertViewModel(repository)

        // Act
        viewModel.onEvent(AlertEvent.OpenAddSheet)

        // Assert
        val sheetState = viewModel.sheetState.value
        assertTrue(sheetState.isVisible)
        assertFalse(sheetState.isEditing)
    }

    @Test
    fun `when SaveAlert is triggered with overlapping time, it shows error and prevents save`() = runTest {
        // Arrange
        val existingAlert = WeatherAlert(id = 1, fromHour = 8, fromMinute = 0, toHour = 9, toMinute = 0, type = AlertType.NOTIFICATION, isActive = true)
        every { repository.getAllAlerts() } returns flowOf(listOf(existingAlert))
        viewModel = AlertViewModel(repository)
        viewModel.uiState.first()

        // Act
        viewModel.onEvent(AlertEvent.OpenAddSheet)
        viewModel.onEvent(AlertEvent.SetFromTime(8, 30))
        viewModel.onEvent(AlertEvent.SetToTime(9, 30))
        viewModel.onEvent(AlertEvent.SaveAlert)

        // Assert
        val sheetState = viewModel.sheetState.value
        assertNotNull("Error message should be populated due to overlap", sheetState.errorMessage)
        assertEquals("This window overlaps with an existing alert", sheetState.errorMessage)

        // Verify
        coVerify(exactly = 0) { repository.insertAlert(any()) }
        coVerify(exactly = 0) { repository.updateAlert(any()) }
    }
}