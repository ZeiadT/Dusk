package iti.mad.dusk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import iti.mad.dusk.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts ORDER BY fromHour ASC, fromMinute ASC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAlert(alert: AlertEntity)

    @Update
    suspend fun updateAlert(alert: AlertEntity)

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteAlert(id: Long)

    @Query("UPDATE alerts SET isActive = :active WHERE id = :id")
    suspend fun setAlertActive(id: Long, active: Boolean)

    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getAlertById(id: Long): AlertEntity?
}
