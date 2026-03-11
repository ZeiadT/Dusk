package iti.mad.dusk.domain.repo

import iti.mad.dusk.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getAllLocations(): Flow<List<Location>>

    fun getDefaultLocation(): Flow<Location?>

    fun isSaved(lat: Double, lon: Double): Flow<Boolean>

    suspend fun saveLocation(location: Location)

    suspend fun setDefaultLocation(lat: Double, lon: Double)

    suspend fun deleteLocation(lat: Double, lon: Double)

    suspend fun clearAll()
}