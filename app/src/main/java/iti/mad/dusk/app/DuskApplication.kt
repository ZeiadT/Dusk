package iti.mad.dusk.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mapbox.common.MapboxOptions
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel
import dagger.hilt.android.HiltAndroidApp
import iti.mad.dusk.core.env.EnvProvider
import javax.inject.Inject

@HiltAndroidApp
class DuskApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var envProvider: EnvProvider

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        MapboxOptions.accessToken = envProvider.mabBoxToken

        val config = ClarityConfig(
            projectId = envProvider.clarityId,
            logLevel = LogLevel.Debug
        )
        Clarity.initialize(applicationContext, config)
    }
}