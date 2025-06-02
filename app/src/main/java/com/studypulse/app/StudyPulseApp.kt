package com.studypulse.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.studypulse.app.nav.AppNavGraph
import kotlinx.coroutines.launch

@Composable
fun StudyPulseApp(
    modifier: Modifier = Modifier,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarState)
        }
    ) { innerPadding ->

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

        AppNavGraph(modifier = modifier.padding(innerPadding))
    }
}