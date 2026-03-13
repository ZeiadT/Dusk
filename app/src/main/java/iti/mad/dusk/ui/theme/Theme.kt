package iti.mad.dusk.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Dark scheme ───────────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = Dark900,
    primaryContainer = OrangeDark,
    onPrimaryContainer = TextDarkPrimary,

    secondary = IceBlue,
    onSecondary = Dark900,
    secondaryContainer = Dark700,
    onSecondaryContainer = TextDarkPrimary,

    background = Dark800,
    onBackground = TextDarkPrimary,

    surface = Dark700,
    onSurface = TextDarkPrimary,
    surfaceVariant = Dark600,
    onSurfaceVariant = TextDarkSecondary,

    outline = Dark500,
    outlineVariant = Dark400,

    error = ErrorRed,
    onError = Light100,
    errorContainer = ErrorRedDim,
    onErrorContainer = ErrorRed,
)

// ── Light scheme ──────────────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary = Orange,
    onPrimary = Light100,
    primaryContainer = OrangeLight,
    onPrimaryContainer = Light100,

    secondary = IceBlue,
    onSecondary = Light100,
    secondaryContainer = Light300,
    onSecondaryContainer = TextLightPrimary,

    background = Light200,
    onBackground = TextLightPrimary,

    surface = Light100,
    onSurface = TextLightPrimary,
    surfaceVariant = Light300,
    onSurfaceVariant = TextLightSecondary,

    outline = Light500,
    outlineVariant = Light600,

    error = ErrorRed,
    onError = Light100,
    errorContainer = ErrorRedDim,
    onErrorContainer = ErrorRed,
)

// ── Theme ─────────────────────────────────────────────────────────────────────

@Composable
fun DuskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color uses wallpaper colors on Android 12+.
    // Set to false to always use the brand orange.
    dynamicColor: Boolean = false, content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Make status bar match background and use correct icon tint
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}