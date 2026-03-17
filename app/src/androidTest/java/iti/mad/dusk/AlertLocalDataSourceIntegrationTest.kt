package iti.mad.dusk

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import iti.mad.dusk.data.local.DuskDatabase
import iti.mad.dusk.data.local.dao.AlertDao
import iti.mad.dusk.data.local.datasource.AlertLocalDataSource
import iti.mad.dusk.data.local.entity.AlertEntity
import iti.mad.dusk.domain.model.AlertType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AlertLocalDataSourceIntegrationTest {

    private lateinit var database: DuskDatabase
    private lateinit var dao: AlertDao
    private lateinit var dataSource: AlertLocalDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database =
            Room.inMemoryDatabaseBuilder(context, DuskDatabase::class.java).allowMainThreadQueries()
                .build()

        dao = database.alertDao()
        dataSource = AlertLocalDataSource(dao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAlert_saves_data_to_DB_and_getAlertById_retrieves_it() = runTest {

        val alert = AlertEntity(
            id = 1L,
            fromHour = 14,
            fromMinute = 30,
            toHour = 15,
            toMinute = 50,
            type = AlertType.NOTIFICATION,
            isActive = true
        )

        // Act
        dataSource.insertAlert(alert)
        val retrievedAlert = dataSource.getAlertById(1L)

        // Assert
        assertEquals(alert.id, retrievedAlert?.id)
        assertEquals(alert.fromHour, retrievedAlert?.fromHour)
        assertEquals(alert.type, retrievedAlert?.type)
    }

    @Test
    fun setAlertActive_updates_the_isActive_flag_in_the_database_successfully() = runTest {
        // Arrange
        val alert = AlertEntity(
            id = 2L,
            fromHour = 8,
            fromMinute = 0,
            toHour = 9,
            toMinute = 0,
            type = AlertType.NOTIFICATION,
            isActive = false
        )
        dataSource.insertAlert(alert)

        // Act
        dataSource.setAlertActive(id = 2L, active = true)

        // Assert
        val updatedAlert = dataSource.getAlertById(2L)
        assertTrue(updatedAlert?.isActive == true)
    }

    @Test
    fun deleteAlert_removes_the_alert_from_the_database_completely() = runTest {
        // Arrange
        val alert = AlertEntity(
            id = 3L,
            fromHour = 9,
            fromMinute = 15,
            toHour = 10,
            toMinute = 15,
            type = AlertType.NOTIFICATION,
            isActive = true
        )
        dataSource.insertAlert(alert)

        // Verify it was inserted
        val insertedAlert = dataSource.getAlertById(3L)
        assertEquals(3L, insertedAlert?.id)

        // Act
        dataSource.deleteAlert(3L)

        // Assert
        val deletedAlert = dataSource.getAlertById(3L)
        assertNull(deletedAlert)
    }

    @Test
    fun getAllAlerts_returns_alerts_ordered_by_fromHour_and_fromMinute() = runTest {
        // Arrange
        val alert1 = AlertEntity(
            id = 1L,
            fromHour = 10,
            fromMinute = 30,
            toHour = 11,
            toMinute = 30,
            type = AlertType.NOTIFICATION,
            isActive = true
        )
        val alert2 = AlertEntity(
            id = 2L,
            fromHour = 8,
            fromMinute = 15,
            toHour = 9,
            toMinute = 15,
            type = AlertType.NOTIFICATION,
            isActive = true
        )
        val alert3 = AlertEntity(
            id = 3L,
            fromHour = 7,
            fromMinute = 10,
            toHour = 8,
            toMinute = 15,
            type = AlertType.NOTIFICATION,
            isActive = true
        )

        dataSource.insertAlert(alert1)
        dataSource.insertAlert(alert2)
        dataSource.insertAlert(alert3)

        // Act
        val alertsList = dataSource.getAllAlerts().first()

        // Assert
        assertEquals(3, alertsList.size)

        assertEquals(3L, alertsList[0].id)
        assertEquals(2L, alertsList[1].id)
        assertEquals(1L, alertsList[2].id)
    }
}