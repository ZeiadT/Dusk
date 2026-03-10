package iti.mad.dusk.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import iti.mad.dusk.data.local.dao.ForecastDao
import iti.mad.dusk.data.local.dao.CurrentWeatherDao
import iti.mad.dusk.data.local.entity.CurrentWeatherCacheEntity
import iti.mad.dusk.data.local.entity.CurrentWeatherConditionEntity
import iti.mad.dusk.data.local.entity.ForecastCacheEntity
import iti.mad.dusk.data.local.entity.ForecastItemEntity
import iti.mad.dusk.data.local.entity.ForecastWeatherConditionEntity

@Database(
    version = 2,
    exportSchema = false,
    entities = [CurrentWeatherCacheEntity::class, CurrentWeatherConditionEntity::class, ForecastCacheEntity::class, ForecastItemEntity::class, ForecastWeatherConditionEntity::class]
)
abstract class DuskDatabase : RoomDatabase() {


    abstract fun weatherCacheDao(): CurrentWeatherDao
    abstract fun forecastCacheDao(): ForecastDao

    companion object {
        const val DATABASE_NAME = "dusk_db"
    }
}