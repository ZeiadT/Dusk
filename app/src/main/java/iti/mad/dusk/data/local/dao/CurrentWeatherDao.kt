package iti.mad.dusk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import iti.mad.dusk.data.local.entity.CurrentWeatherCacheEntity
import iti.mad.dusk.data.local.entity.CurrentWeatherConditionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {

    @Query("SELECT * FROM current_weather WHERE locationID = :locationId LIMIT 1")
    fun getWeatherByLocation(locationId: String): Flow<CurrentWeatherCacheEntity?>

    @Query(
        """
        SELECT * FROM current_weather_condition
        WHERE owner_location_id = :locationId
        ORDER BY position ASC
        """
    )
    suspend fun getConditionsByLocation(locationId: String): List<CurrentWeatherConditionEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(entity: CurrentWeatherCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConditions(conditions: List<CurrentWeatherConditionEntity>)

    @Transaction
    suspend fun insertWeatherWithConditions(
        entity: CurrentWeatherCacheEntity,
        conditions: List<CurrentWeatherConditionEntity>
    ) {
        insertWeather(entity)
        insertConditions(conditions)
    }

    @Query(
        """
        DELETE FROM current_weather
        WHERE ((:now) - cached_at) > :ttlMillis
        """
    )
    suspend fun clearExpired(
        now: Long,
        ttlMillis: Long
    )
}