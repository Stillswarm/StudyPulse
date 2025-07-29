package com.studypulse.app.feat.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.nav.NavigableRoute
import com.studypulse.app.nav.Route
import com.studypulse.app.ui.theme.Blue
import com.studypulse.app.ui.theme.Cyan
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.LightGray
import com.studypulse.app.ui.theme.Lime
import com.studypulse.app.ui.theme.Orange
import com.studypulse.app.ui.theme.Pink
import com.studypulse.app.ui.theme.Purple
import com.studypulse.app.ui.theme.WhiteSecondary
import kotlinx.coroutines.launch

data class HomeScreenItem(
    val name: String,
    val desc: String,
    val icon: Int,
    val color: Color,
    val active: Boolean,
    val route: NavigableRoute?,
)

val homeScreenItems = listOf(
    HomeScreenItem(
        "Attendance Section",
        "Your Ultimate Bunk-Mate",
        R.drawable.ic_calendar,
        Gold,
        active = true,
        route = Route.AttendanceRoute
    ),
    HomeScreenItem(
        "Exam Portal",
        "Manage your assessments",
        icon = R.drawable.ic_notes,
        color = Purple,
        active = false,
        route = null,
    ),
    HomeScreenItem(
        "Tasks and Deadlines",
        "Stay on top of your work",
        R.drawable.ic_clock,
        Pink,
        active = false,
        route = null,
    ),
    HomeScreenItem(
        "Material Dump",
        "Access your study resources",
        icon = R.drawable.ic_file,
        color = Orange,
        active = false,
        route = null,
    ),
    HomeScreenItem(
        "Community",
        "Connect with peers",
        icon = R.drawable.ic_group,
        color = Blue,
        active = false,
        route = null,
    ),
    HomeScreenItem(
        "Flashcards",
        "Revisions made easy",
        R.drawable.ic_flashcards,
        Cyan,
        active = false,
        route = null,
    ),
    HomeScreenItem(
        "Budget Tracker",
        "Manage your expenses",
        icon = R.drawable.ic_bar_graph,
        color = Lime,
        active = false,
        route = null,
    ),
)

@Composable
fun HomeScreen(
    navigate: (NavigableRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        AppTopBar(
            backgroundColor = DarkGray,
            foregroundGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    Pair(0.0f, Gold),
                    Pair(1f, DarkGray)
                )
            ),
            title = "Study Pulse",
            navigationIcon = R.drawable.logo_pulse,
            onNavigationClick = { scope.launch { NavigationDrawerController.toggle() } },
            actionIcon = R.drawable.ic_profile,
            onActionClick = { navigate(Route.ProfileRoute) },
            titleColor = WhiteSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn {
            itemsIndexed(homeScreenItems) { idx, item ->
                HomeScreenBox(
                    item = item,
                    modifier = Modifier
                        .padding(12.dp)
                        .noRippleClickable { if (item.route != null) navigate(item.route) },
                    contentLeft = idx % 2 != 0
                )
            }
        }
    }
}

@Composable
fun HomeScreenBox(item: HomeScreenItem, contentLeft: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, DarkGray, RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        item.color,
                        item.color.copy(alpha = 0.50f),
                    )
                )
            )
    ) {
        Icon(
            painter = painterResource(item.icon),
            contentDescription = item.name,
            tint = DarkGray,
            modifier = Modifier
                .align(if (contentLeft) Alignment.BottomEnd else Alignment.BottomStart)
                .rotate(if (contentLeft) 30f else -30f)
                .size(64.dp)
        )

        Column(
            modifier = Modifier
                .align(if (contentLeft) Alignment.TopStart else Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = item.name,
                color = DarkGray,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = item.desc,
                color = DarkGray,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }

        if (!item.active) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(LightGray.copy(alpha = 0.5f))
            )

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RectangleShape)
                    .background(DarkGray.copy(alpha = 0.60f))
                    .align(Alignment.BottomEnd)
            ) {
                Text(
                    text = "Coming Soon",
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}