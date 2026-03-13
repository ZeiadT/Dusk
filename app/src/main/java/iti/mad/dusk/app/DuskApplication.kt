package iti.mad.dusk.app

import android.app.Application
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel
import dagger.hilt.android.HiltAndroidApp
import iti.mad.dusk.core.env.EnvProvider
import javax.inject.Inject

@HiltAndroidApp
class DuskApplication @Inject constructor() : Application() {

    @Inject
    lateinit var envProvider: EnvProvider

    override fun onCreate() {
        super.onCreate()

        val config = ClarityConfig(
            projectId = envProvider.clarityId,
            logLevel = LogLevel.Debug
        )
//        Clarity.initialize(applicationContext, config)
        //todo remove comment on production
    }
}