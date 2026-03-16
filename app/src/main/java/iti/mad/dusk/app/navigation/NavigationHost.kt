package iti.mad.dusk.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import iti.mad.dusk.ui.presentation.alert.AlertScreen
import iti.mad.dusk.ui.presentation.locations.LocationsScreen
import iti.mad.dusk.ui.presentation.home.HomeScreen
import iti.mad.dusk.ui.presentation.map.MapScreen
import iti.mad.dusk.ui.presentation.setting.SettingScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(200)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) }
    ) {
        composable<Screen.Home> {
            HomeScreen()
        }
        composable<Screen.Alert> {
            AlertScreen()
        }
        composable<Screen.Locations> {
            LocationsScreen { navController.navigate(Screen.Map) }
        }
        composable<Screen.Settings> {
            SettingScreen()
        }
        composable<Screen.Map> {
            MapScreen() { navController.popBackStack() }
        }
    }
}