package iti.mad.dusk.core.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import iti.mad.dusk.data.local.DuskDatabase
import iti.mad.dusk.data.local.dao.ForecastDao
import iti.mad.dusk.data.local.dao.CurrentWeatherDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDuskDatabase(application: Application): DuskDatabase = Room.databaseBuilder(
        application, DuskDatabase::class.java, DuskDatabase.DATABASE_NAME
    ).fallbackToDestructiveMigration(true).build()

    @Provides
    @Singleton
    fun provideWeatherCacheDao(duskDatabase: DuskDatabase): CurrentWeatherDao =
        duskDatabase.weatherCacheDao()

    @Provides
    @Singleton
    fun provideForecastCacheDao(duskDatabase: DuskDatabase): ForecastDao =
        duskDatabase.forecastCacheDao()
}