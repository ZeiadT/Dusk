package iti.mad.dusk.ui.presentation.main

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import iti.mad.dusk.app.navigation.DuskBottomNavBar
import iti.mad.dusk.app.navigation.NavigationHost

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(LocalActivity.current as ViewModelStoreOwner)
) {
    val scaffoldState by mainViewModel.scaffoldState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier.fillMaxSize(),
            bottomBar = { if(scaffoldState.showBottomBar) DuskBottomNavBar(navController = navController) },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = scaffoldState.fabState != null,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    scaffoldState.fabState?.let { fab ->
                        ExtendedFloatingActionButton(
                            icon = { Icon(fab.icon, contentDescription = null) },
                            text = { Text(fab.label) },
                            onClick = fab.onClick
                        )
                    }
                }
            },
        ) { innerPadding ->
            NavigationHost(
                navController = navController, modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}