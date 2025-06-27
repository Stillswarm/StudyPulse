package com.studypulse.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.studypulse.app.nav.AppNavGraph
import kotlinx.coroutines.launch

@Composable
fun StudyPulseApp(
    modifier: Modifier = Modifier,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RectangleShape) {
                Icon(
                    painter = painterResource(R.drawable.logo_pulse),
                    contentDescription = "brand logo",
                    tint = Color.Unspecified
                )
            }
        }
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            AppNavGraph()

            SnackbarHost(
                hostState = snackbarState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
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