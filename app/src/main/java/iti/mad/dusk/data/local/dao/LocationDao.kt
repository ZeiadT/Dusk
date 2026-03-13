package iti.mad.dusk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import iti.mad.dusk.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface LocationDao {

    @Query("SELECT * FROM location ORDER BY added_at DESC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM location WHERE is_default = 1 LIMIT 1")
    fun getDefaultLocation(): Flow<LocationEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM location WHERE locationID = :locationId)")
    fun isSaved(locationId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(entity: LocationEntity)

    @Transaction
    suspend fun updateCurrentLocation(lat: Double, lon: Double, cityName: String, country: String, displayName: String) {
        val oldLocation = getLiveLocationInternal()
        val defaultLocation = getDefaultLocation().firstOrNull()
        clearCurrent()

        val newLocation = LocationEntity(
            locationID = LocationEntity.locationId(lat, lon),
            lat = lat,
            lon = lon,
            country = country,
            cityName = cityName,
            displayName = displayName,
            isDefault = oldLocation?.isDefault ?: (defaultLocation == null),
            isCurrent = true,
            addedAt = System.currentTimeMillis()
        )
        insertLocation(newLocation)
    }

    @Query("SELECT * FROM location WHERE is_current = 1 LIMIT 1")
    suspend fun getLiveLocationInternal(): LocationEntity?

    @Transaction
    suspend fun setDefaultById(locationId: String) {
        clearDefault()
        setDefault(locationId)
    }

    @Query("UPDATE location SET is_default = 1 WHERE locationID = :locationId")
    suspend fun setDefault(locationId: String)

    @Query("UPDATE location SET is_default = 0 WHERE is_default = 1")
    suspend fun clearDefault()

    @Query("DELETE FROM location WHERE is_current = 1")
    suspend fun clearCurrent()

    @Query("DELETE FROM location WHERE locationID = :locationId")
    suspend fun deleteLocation(locationId: String)

    @Query("DELETE FROM location")
    suspend fun clearAll()

}