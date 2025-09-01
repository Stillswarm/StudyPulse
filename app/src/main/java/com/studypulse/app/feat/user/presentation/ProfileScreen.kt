package com.studypulse.app.feat.user.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AllSemestersBottomSheet
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.components.SemesterBottomSheetItem
import com.studypulse.app.common.ui.modifier.gradientFill
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.LightGray
import com.studypulse.app.ui.theme.WarmWhite
import com.studypulse.app.ui.theme.WhiteSecondary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onAddNewSemester: () -> Unit,
    navigateToLogin: () -> Unit,
    vm: ProfileScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                vm.fetchInitialData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DarkGray)
        } else if (state.user == null) {
            Text(
                text = "Unable to fetch profile data. Please try again later.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp).align(Alignment.Center)
            )
        } else {
            state.user?.let { user ->
                Column {
                    AppTopBar(
                        backgroundColor = DarkGray,
                        foregroundGradient = null,
                        title = "Profile",
                        navigationIcon = R.drawable.logo_pulse,
                        onNavigationClick = { scope.launch { NavigationDrawerController.toggle() } },
                        actionIcon = null,
                        onActionClick = null,
                        titleColor = WarmWhite
                    )

                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(Color.White),
                            )

                            Row(
                                modifier = Modifier
                                    .height(220.dp)
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                                    .align(Alignment.BottomCenter),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .clip(CircleShape)
                                        .background(DarkGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.name?.substring(0..<1)?.uppercase()
                                            ?: user.email.substring(0..<1).uppercase(),
                                        fontSize = 96.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .gradientFill(
                                                gradient = Brush.linearGradient(
                                                    colorStops = arrayOf(
                                                        Pair(0f, Gold),
                                                        Pair(100f, Color(0xFF716005))
                                                    )
                                                )
                                            )
                                    )
                                }

                                Image(
                                    painter = painterResource(R.drawable.logo_pulse),
                                    modifier = Modifier
                                        .weight(1f),
                                    contentDescription = "logo",
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = user.email,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                letterSpacing = 1.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Name",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Icon(
                                    painter = painterResource(if (state.editingName) R.drawable.ic_check else R.drawable.ic_edit),
                                    contentDescription = "Edit",
                                    modifier = Modifier.noRippleClickable {
                                        if (state.editingName) vm.saveName()
                                        else vm.editName()
                                    },
                                )
                            }

                            TextField(
                                value = if (state.editingName) state.currentName!! else (user.name
                                    ?: ""),
                                onValueChange = { vm.updateName(it) },
                                enabled = state.editingName,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = LightGray,
                                    focusedContainerColor = Color.White,
                                    disabledContainerColor = LightGray
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Institution",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Icon(
                                    painter = painterResource(if (state.editingInstitution) R.drawable.ic_check else R.drawable.ic_edit),
                                    contentDescription = "Edit",
                                    modifier = Modifier.noRippleClickable {
                                        if (state.editingInstitution) vm.saveInstitution()
                                        else vm.editInstitution()
                                    },
                                )
                            }

                            TextField(
                                value = if (state.editingInstitution) state.currentInstitution!! else (user.institution
                                    ?: "--"),
                                onValueChange = { vm.updateInstitution(it) },
                                enabled = state.editingInstitution,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    unfocusedContainerColor = LightGray,
                                    disabledContainerColor = LightGray,
                                    focusedContainerColor = Color.White
                                )
                            )

                            // active semester info and bottom sheet
                            state.currentSemester?.let { sem ->
                                Text(
                                    text = "Current Semester",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                val semSheetState = rememberModalBottomSheetState()

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LightGray)
                                ) {
                                    SemesterBottomSheetItem(
                                        selectedSemester = sem.id,
                                        semester = sem,
                                        onSemesterClick = {
                                            scope.launch { semSheetState.show() }
                                        },
                                        buttonColor = DarkGray,
                                    )
                                }

                                AllSemestersBottomSheet(
                                    sheetState = semSheetState,
                                    semesterList = state.semesterList,
                                    onSemesterClick = {
                                        scope.launch { semSheetState.hide() }
                                        vm.updateCurrentSemester(it)
                                    },
                                    buttonColor = DarkGray,
                                    onDismiss = { scope.launch { semSheetState.hide() } },
                                    selectedSemesterId = sem.id,
                                    onAddSemester = onAddNewSemester
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkGray)
                                    .noRippleClickable { vm.signOut(navigateToLogin) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Settings,
                                        contentDescription = null,
                                        tint = WhiteSecondary,
                                    )

                                    Text(
                                        text = "Sign Out",
                                        fontSize = 14.sp,
                                        color = WhiteSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}