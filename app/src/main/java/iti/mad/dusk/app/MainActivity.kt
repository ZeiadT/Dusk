package iti.mad.dusk.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import iti.mad.dusk.domain.model.Language
import iti.mad.dusk.domain.repo.SettingsRepository
import iti.mad.dusk.ui.presentation.main.MainScreen
import iti.mad.dusk.ui.theme.DuskTheme
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        applyLocaleFromSettings()

        setContent {
            val navController = rememberNavController()
            DuskTheme {
                MainScreen(navController)
            }
        }
    }


    private fun applyLocaleFromSettings() {
        val language = runBlocking {
            settingsRepository.getSettings().first().language
        }
        applyLocale(language)
    }

    fun applyLocale(language: Language) {
        val locale = Locale.forLanguageTag(language.code)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        baseContext.createConfigurationContext(config)
        @Suppress("DEPRECATION") resources.updateConfiguration(config, resources.displayMetrics)
    }
}