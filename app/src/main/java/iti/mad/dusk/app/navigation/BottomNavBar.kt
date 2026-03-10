package iti.mad.dusk.app.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun DuskBottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
//        containerColor = ,
//        contentColor =
    ) {
        bottomNavItems.forEach { navItem ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(navItem.screen::class)
            } == true

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "NavItemScale_${navItem.label}"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(navItem.screen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                        contentDescription = navItem.label,
                        modifier = Modifier.scale(scale)
                    )
                },
                label = {
                    Text(
                        text = navItem.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
//                colors = NavigationBarItemDefaults.colors(
//                    selectedIconColor = ,
//                    selectedTextColor = ,
//                    unselectedIconColor = ,
//                    unselectedTextColor = ,
//                    indicatorColor =
//                )
            )
        }
    }
}