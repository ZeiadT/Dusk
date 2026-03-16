package iti.mad.dusk.data.local.datasource

import iti.mad.dusk.core.util.extension.roundCoordinate
import iti.mad.dusk.data.local.dao.LocationDao
import iti.mad.dusk.data.local.entity.LocationEntity
import iti.mad.dusk.domain.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LocationLocalDataSource @Inject constructor(private val locationDao: LocationDao) {

    fun getAllLocations(): Flow<List<LocationEntity>> =
        locationDao.getAllLocations()

    fun getDefaultLocation(): Flow<LocationEntity?> =
        locationDao.getDefaultLocation()

    fun isSaved(lat: Double, lon: Double): Flow<Boolean> =
        locationDao.isSaved(LocationEntity.locationId(lat.roundCoordinate(), lon.roundCoordinate()))

    suspend fun isDefaultLocation(lat: Double, lon: Double): Boolean {
        val location = locationDao.getDefaultLocation().firstOrNull() ?: return false

        return (location.lat == lat.roundCoordinate() && location.lon == lon.roundCoordinate())
    }

    suspend fun isCurrentLocation(lat: Double, lon: Double): Boolean {
        val location = locationDao.getLiveLocationInternal() ?: return false

        return (location.lat == lat.roundCoordinate() && location.lon == lon.roundCoordinate())
    }

    suspend fun insertLocation(entity: LocationEntity) {
        val lat = entity.lat.roundCoordinate()
        val lon = entity.lon.roundCoordinate()
        val locationID = LocationEntity.locationId(lat, lon)
        locationDao.insertLocation(
            entity.copy(
                lat = lat,
                lon = lon,
                locationID = locationID
            )
        )
    }


    suspend fun setDefaultLocation(lat: Double, lon: Double) =
        locationDao.setDefaultById(
            LocationEntity.locationId(lat.roundCoordinate(), lon.roundCoordinate())
        )


    suspend fun setCurrentLocation(entity: LocationEntity) =
        locationDao.updateCurrentLocation(
            entity.lat.roundCoordinate(),
            entity.lon.roundCoordinate(),
            entity.cityName,
            entity.country,
            entity.displayName
        )

    suspend fun deleteLocation(lat: Double, lon: Double) =
        locationDao.deleteLocation(
            LocationEntity.locationId(lat.roundCoordinate(), lon.roundCoordinate())
        )

    suspend fun clearAll() = locationDao.clearAll()
}