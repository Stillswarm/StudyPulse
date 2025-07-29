package com.studypulse.app.common.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.home.ui.homeScreenItems
import com.studypulse.app.nav.NavigableRoute
import com.studypulse.app.nav.Route
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.LightGray
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawerContent(
    navigate: (NavigableRoute) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    ModalDrawerSheet(
        drawerState = drawerState,
        drawerShape = RectangleShape,
        drawerContainerColor = LightGray,
        drawerContentColor = DarkGray,
        modifier = modifier,
    ) {
        Spacer(Modifier.height(8.dp))
        homeScreenItems.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch { NavigationDrawerController.toggle() }
                        if (item.route != null) {
                            navigate(item.route)
                        } else {
                            scope.launch {
                                SnackbarController.sendEvent(
                                    SnackbarEvent("This feature is not yet available")
                                )
                            }
                        }

                        scope.launch { NavigationDrawerController.toggle() }
                    }
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = item.name,
                    letterSpacing = (0.5).sp,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // bottom part
        HorizontalDivider()

        Text(
            text = "Feedback/Bug Report",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch { NavigationDrawerController.toggle() }
                    navigate(Route.FeedbackRoute)
                }
                .padding(16.dp)
        )

        HorizontalDivider()

        Text(
            text = "Profile",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch { NavigationDrawerController.toggle() }
                    navigate(Route.ProfileRoute)
                }
                .padding(16.dp),
        )

        HorizontalDivider()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.logo_pulse),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "StudyPulse",
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp),
//                textAlign = TextAlign.Center
            )
        }
    }
}