package iti.mad.dusk.data.local.datasource

import iti.mad.dusk.core.util.extension.roundCoordinate
import iti.mad.dusk.data.local.dao.LocationDao
import iti.mad.dusk.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationLocalDataSource @Inject constructor(private val locationDao: LocationDao) {

    fun getAllLocations(): Flow<List<LocationEntity>> =
        locationDao.getAllLocations()

    fun getDefaultLocation(): Flow<LocationEntity?> =
        locationDao.getDefaultLocation()

    fun isSaved(lat: Double, lon: Double): Flow<Boolean> =
        locationDao.isSaved(LocationEntity.locationId(lat.roundCoordinate(), lon.roundCoordinate()))

    suspend fun insertLocation(entity: LocationEntity) =
        locationDao.insertLocation(entity)

    suspend fun setDefaultLocation(lat: Double, lon: Double) =
        locationDao.setDefaultById(
            LocationEntity.locationId(lat.roundCoordinate(), lon.roundCoordinate())
        )


    suspend fun setCurrentLocation(entity: LocationEntity) =
        locationDao.updateCurrentLocation(entity.lat, entity.lon, entity.cityName, entity.country, entity.displayName)

    suspend fun deleteLocation(lat: Double, lon: Double) =
        locationDao.deleteLocation(
            LocationEntity.locationId(lat.roundCoordinate(), lon.roundCoordinate())
        )

    suspend fun clearAll() = locationDao.clearAll()
}