package iti.mad.dusk.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent
import iti.mad.dusk.data.repo.LocationRepositoryImpl
import iti.mad.dusk.domain.repo.WeatherRepository
import iti.mad.dusk.data.repo.WeatherRepositoryImpl
import iti.mad.dusk.domain.repo.LocationRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository
}