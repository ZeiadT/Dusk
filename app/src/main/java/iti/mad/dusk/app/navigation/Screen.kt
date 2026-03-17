package iti.mad.dusk.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector
import iti.mad.dusk.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable data object Home : Screen()
    @Serializable data object Alert : Screen()
    @Serializable data object Locations : Screen()
    @Serializable data object Map : Screen()
    @Serializable data object Settings : Screen()
}

data class BottomNavItem(
    val screen: Screen,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        labelRes = R.string.nav_weather,
        selectedIcon = Icons.Filled.WbSunny,
        unselectedIcon = Icons.Outlined.WbSunny,
    ),
    BottomNavItem(
        screen = Screen.Alert,
        labelRes = R.string.nav_alerts,
        selectedIcon = Icons.Filled.Cloud,
        unselectedIcon = Icons.Outlined.Cloud,
    ),
    BottomNavItem(
        screen = Screen.Locations,
        labelRes = R.string.nav_locations,
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map,
    ),
    BottomNavItem(
        screen = Screen.Settings,
        labelRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    ),
)