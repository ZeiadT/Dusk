package iti.mad.dusk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import iti.mad.dusk.app.navigation.DuskBottomNavBar
import iti.mad.dusk.app.navigation.NavigationHost
import iti.mad.dusk.ui.theme.DuskTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            DuskTheme {
                DuskApp(navController)
            }
        }
    }
}

@Composable
fun DuskApp(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(), bottomBar = {
            DuskBottomNavBar(navController = navController)
        }) { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}