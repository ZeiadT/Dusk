package iti.mad.dusk.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import iti.mad.dusk.core.alert.AlertScheduler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlertModule {

    @Provides
    @Singleton
    fun provideAlertScheduler(
        @ApplicationContext context: Context
    ): AlertScheduler = AlertScheduler(context)
}