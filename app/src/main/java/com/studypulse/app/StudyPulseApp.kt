package com.studypulse.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.studypulse.app.common.ui.screen.NavigationDrawerContent
import com.studypulse.app.nav.AppNavGraph
import com.studypulse.app.nav.Route
import com.studypulse.app.ui.theme.WhiteSecondary
import kotlinx.coroutines.launch

@Composable
fun StudyPulseApp(
    modifier: Modifier = Modifier,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController: NavHostController = rememberNavController()

    // Main content including navigation and drawers
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NavigationDrawerContent(
                    navigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Route.HomeRoute) {
                                inclusive = false
                            }
                        }
                    },
                    drawerState = drawerState
                )
            }
        ) {
            Surface(color = WhiteSecondary) {
                AppNavGraph(navController = navController)
            }
        }

        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { /* no-op, we only dismiss via the hostState */ }
        ) {
            SnackbarHost(
                hostState = snackbarState,
                modifier = Modifier

            )
        }
    }

    ObserveAsEvents(SnackbarController.events) {
        scope.launch {
            snackbarState.currentSnackbarData?.dismiss()

            val result = snackbarState.showSnackbar(
                message = it.message,
                actionLabel = it.action?.text,
            )

            if (result == SnackbarResult.ActionPerformed) {
                it.action?.action?.invoke()
            }
        }
    }

    ObserveAsEvents(NavigationDrawerController.events) {
        scope.launch {
            if (drawerState.isClosed) drawerState.open()
            else drawerState.close()
        }
    }
}