package iti.mad.dusk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import iti.mad.dusk.data.local.entity.FORECAST_TTL_MS
import iti.mad.dusk.data.local.entity.ForecastCacheEntity
import iti.mad.dusk.data.local.entity.ForecastItemEntity
import iti.mad.dusk.data.local.entity.ForecastWeatherConditionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Query("SELECT * FROM forecast WHERE locationID = :locationId LIMIT 1")
    fun getForecastByLocation(locationId: String): Flow<ForecastCacheEntity?>

    @Query(
        """
        SELECT * FROM forecast_item
        WHERE owner_location_id = :locationId
        ORDER BY dt ASC
        """
    )
    suspend fun getItemsByLocation(locationId: String): List<ForecastItemEntity>

    @Query(
        """
        SELECT * FROM forecast_weather_condition
        WHERE owner_location_id = :locationId
        ORDER BY owner_dt ASC, position ASC
        """
    )
    suspend fun getConditionsByLocation(locationId: String): List<ForecastWeatherConditionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastCache(entity: ForecastCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ForecastItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConditions(conditions: List<ForecastWeatherConditionEntity>)

    @Transaction
    suspend fun insertForecastWithItems(
        cache: ForecastCacheEntity,
        items: List<ForecastItemEntity>,
        conditions: List<ForecastWeatherConditionEntity>
    ) {
        insertForecastCache(cache)
        insertItems(items)
        insertConditions(conditions)
    }

    @Query(
        """
        DELETE FROM forecast
        WHERE ((:now) - cached_at) > :ttlMillis
        """
    )
    suspend fun clearExpired(
        now: Long,
        ttlMillis: Long
    )
}
