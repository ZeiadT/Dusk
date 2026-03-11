package iti.mad.dusk.data.repo

import iti.mad.dusk.data.local.datasource.LocationLocalDataSource
import iti.mad.dusk.data.mapper.toDomain
import iti.mad.dusk.data.mapper.toEntity
import iti.mad.dusk.domain.model.Location
import iti.mad.dusk.domain.repo.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationLocalDataSource: LocationLocalDataSource) :
    LocationRepository {

    override fun getAllLocations(): Flow<List<Location>> =
        locationLocalDataSource.getAllLocations().map { list ->
            list.map { it.toDomain() }
        }

    override fun getDefaultLocation(): Flow<Location?> =
        locationLocalDataSource.getDefaultLocation().map { it?.toDomain() }

    override fun isSaved(lat: Double, lon: Double): Flow<Boolean> =
        locationLocalDataSource.isSaved(lat, lon)

    override suspend fun saveLocation(location: Location) =
        locationLocalDataSource.insertLocation(location.toEntity())

    override suspend fun setDefaultLocation(lat: Double, lon: Double) =
        locationLocalDataSource.setDefaultLocation(lat, lon)

    override suspend fun deleteLocation(lat: Double, lon: Double) =
        locationLocalDataSource.deleteLocation(lat, lon)

    override suspend fun clearAll() = locationLocalDataSource.clearAll()

}