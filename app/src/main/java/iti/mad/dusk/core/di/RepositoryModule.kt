package iti.mad.dusk.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent
import iti.mad.dusk.data.repo.AlertRepositoryImpl
import iti.mad.dusk.data.repo.LocationRepositoryImpl
import iti.mad.dusk.data.repo.SettingsRepositoryImpl
import iti.mad.dusk.domain.repo.WeatherRepository
import iti.mad.dusk.data.repo.WeatherRepositoryImpl
import iti.mad.dusk.domain.repo.AlertRepository
import iti.mad.dusk.domain.repo.LocationRepository
import iti.mad.dusk.domain.repo.SettingsRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(alertRepositoryImpl: AlertRepositoryImpl): AlertRepository


    @Binds
    @Singleton
    abstract fun bindSettingsRepository(settingsRepository: SettingsRepositoryImpl): SettingsRepository
}