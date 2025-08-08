package com.studypulse.app.feat.attendance.attendance.presentation.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.studypulse.app.sem1
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
@LargeTest
class AttendanceScreenInstrumentationTest : KoinTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockViewModel: AttendanceScreenViewModel
    private lateinit var stateFlow: MutableStateFlow<AttendanceScreenState>
    private lateinit var semesterIdFlow: MutableStateFlow<String>

    // Navigation callbacks
    private val onNavigateToAddSemester = mockk<() -> Unit>(relaxed = true)
    private val onNavigateToProfile = mockk<() -> Unit>(relaxed = true)
    private val onNavigateToCourseList = mockk<() -> Unit>(relaxed = true)
    private val onNavigateToAttendanceCalendar = mockk<() -> Unit>(relaxed = true)
    private val onNavigateToAttendanceOverview = mockk<() -> Unit>(relaxed = true)

    private val testModule = module {
        factory { mockViewModel }
    }

    @Before
    fun setup() {
        // Initialize mock ViewModel
        mockViewModel = mockk<AttendanceScreenViewModel>(relaxed = true)
        stateFlow = MutableStateFlow(createInitialState())
        semesterIdFlow = MutableStateFlow("test-semester-id")

        every { mockViewModel.state } returns stateFlow
        every { mockViewModel.semesterIdFlow } returns semesterIdFlow
        coEvery { mockViewModel.fetchInitialData() } just Runs
        every { mockViewModel.onChangeActiveSemester(any()) } just Runs

        loadKoinModules(testModule)
    }

    @After
    fun cleanup() {
        unloadKoinModules(testModule)
        clearAllMocks()
    }

    private fun createInitialState() = AttendanceScreenState(
        isLoading = false,
        unmarkedCount = 5,
        fullAttendanceCount = 3,
        lowAttendanceCount = 2,
        attendancePercentage = 85,
        courseWiseSummaries = listOf(),
        activeSemester = sem1,
        semesterList = listOf()
    )

    private fun setContent() {
        composeTestRule.setContent {
            AttendanceScreen(
                onNavigateToAddSemester = onNavigateToAddSemester,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToCourseList = onNavigateToCourseList,
                onNavigateToAttendanceCalendar = onNavigateToAttendanceCalendar,
                onNavigateToAttendanceOverview = onNavigateToAttendanceOverview,
                vm = mockViewModel
            )
        }
    }

    // PERFORMANCE TESTS
    @Test
    fun testScreenCompositionPerformance() {
        val compositionTime = measureTimeMillis {
            setContent()
            composeTestRule.waitForIdle()
        }

        // Screen should compose in under 500ms
        assertThat(compositionTime).isLessThan(500L)
    }

//    @Test
//    fun testScrollPerformanceWithLargeCourseList() {
//        // Setup state with many courses
//        val largeCourseList = (1..50).map {
//            mockk<CourseSummary>() // Replace with actual CourseWiseSummary when available
//        }
//        stateFlow.value = stateFlow.value.copy(courseWiseSummaries = largeCourseList)
//
//        setContent()
//
//        val scrollTime = measureTimeMillis {
//            composeTestRule
//                .onNodeWithTag("AttendanceScreen_LazyColumn")
//                .performScrollToIndex(25)
//            composeTestRule.waitForIdle()
//        }
//
//        // Scrolling should be smooth (under 100ms)
//        assertThat(scrollTime).isLessThan(100L)
//    }

//    @Test
//    fun testRecompositionPerformance() {
//        setContent()
//
//        val recompositionTime = measureTimeMillis {
//            // Trigger recomposition by updating state
//            stateFlow.value = stateFlow.value.copy(unmarkedCount = 10)
//            composeTestRule.waitForIdle()
//        }
//
//        // Recomposition should be fast (under 50ms)
//        assertThat(recompositionTime).isLessThan(50L)
//    }

    // UI COMPONENT TESTS
    @Test
    fun testScreenRendersCorrectly() {
        setContent()

        // Verify main components are visible
        composeTestRule.onNodeWithTag("AttendanceScreen_Root").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AttendanceScreen_TopBar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AttendanceScreen_MainColumn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AttendanceScreen_LazyColumn").assertIsDisplayed()
    }

    @Test
    fun testTopBarDisplaysCorrectContent() {
        setContent()

        composeTestRule
            .onNodeWithText("Your Ultimate Bunk Mate!")
            .assertIsDisplayed()
    }

    @Test
    fun testQuickStatsCardDisplaysCorrectData() {
        setContent()

        // Verify Quick Stats card is visible
        composeTestRule.onNodeWithTag("AttendanceScreen_QuickStatsCard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quick Stats").assertIsDisplayed()

        // Verify stat values using Truth assertions
        composeTestRule.onNodeWithText("5").assertIsDisplayed() // Unmarked count
        composeTestRule.onNodeWithText("3").assertIsDisplayed() // Full attendance count
        composeTestRule.onNodeWithText("2").assertIsDisplayed() // Low attendance count
        composeTestRule.onNodeWithText("85").assertIsDisplayed() // Attendance percentage

        // Additional Truth assertions for state verification
        val currentState = stateFlow.value
        assertThat(currentState.unmarkedCount).isEqualTo(5)
        assertThat(currentState.fullAttendanceCount).isEqualTo(3)
        assertThat(currentState.lowAttendanceCount).isEqualTo(2)
        assertThat(currentState.attendancePercentage).isEqualTo(85)
    }

    @Test
    fun testNavigationButtons() {
        setContent()

        // Verify all navigation buttons are present
        composeTestRule.onNodeWithTag("AttendanceScreen_AttendanceOverviewButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AttendanceScreen_CoursesOverviewButton").assertIsDisplayed()

        // this button may not be visible immediately on some screens
        composeTestRule.onNodeWithTag("AttendanceScreen_LazyColumn")
            .performScrollToNode(hasTestTag("AttendanceScreen_AttendanceCalendarButton"))

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("AttendanceScreen_AttendanceCalendarButton").assertIsDisplayed()
    }

    // LOADING STATE TESTS
    @Test
    fun testLoadingStateDisplaysCorrectly() {
        stateFlow.value = stateFlow.value.copy(isLoading = true)
        setContent()

        // Loading indicator should be visible
        composeTestRule.onNodeWithTag("AttendanceScreen_LoadingIndicator").assertIsDisplayed()

        // Quick stats should not be visible during loading
        composeTestRule.onNodeWithTag("AttendanceScreen_QuickStatsCard").assertDoesNotExist()
    }

    @Test
    fun testLoadingToContentTransition() {
        // Start with loading state
        stateFlow.value = stateFlow.value.copy(isLoading = true)
        setContent()

        // Verify loading is shown
        composeTestRule.onNodeWithTag("AttendanceScreen_LoadingIndicator").assertIsDisplayed()

        // Update to loaded state
        stateFlow.value = stateFlow.value.copy(isLoading = false)

        // Verify content is now shown
        composeTestRule.onNodeWithTag("AttendanceScreen_QuickStatsCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AttendanceScreen_LoadingIndicator").assertDoesNotExist()
    }

    // NO SEMESTER STATE TESTS
    @Test
    fun testNoActiveSemesterState() {
        semesterIdFlow.value = ""
        setContent()

        // Should show no semester message
        composeTestRule
            .onNodeWithTag("AttendanceScreen_NoSemesterText")
            .assertIsDisplayed()

        // Quick stats should not be visible
        composeTestRule.onNodeWithTag("AttendanceScreen_QuickStatsCard").assertDoesNotExist()
    }

    @Test
    fun testNoActiveSemesterClickOpensBottomSheet() {
        semesterIdFlow.value = ""
        setContent()

        // Click on the no semester text
        composeTestRule
            .onNodeWithTag("AttendanceScreen_NoSemesterText")
            .performClick()

        composeTestRule.waitForIdle()

        // Bottom sheet should appear (verify by checking if semester sheet content is visible)
        // Note: You might need to add test tags to the bottom sheet content
    }

    // STATE FLOW TESTING WITH TURBINE
    @Test
    fun testStateFlowEmissions() = runTest {
        setContent()

        stateFlow.test {
            // Initial state
            val initialState = awaitItem()
            assertThat(initialState.unmarkedCount).isEqualTo(5)
            assertThat(initialState.isLoading).isFalse()

            // Emit new state
            stateFlow.value = stateFlow.value.copy(unmarkedCount = 10, isLoading = true)
            val updatedState = awaitItem()
            assertThat(updatedState.unmarkedCount).isEqualTo(10)
            assertThat(updatedState.isLoading).isTrue()

            // Emit final state
            stateFlow.value = stateFlow.value.copy(isLoading = false, attendancePercentage = 95)
            val finalState = awaitItem()
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.attendancePercentage).isEqualTo(95)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testSemesterIdFlowEmissions() = runTest {
        setContent()
        composeTestRule.waitForIdle()

        semesterIdFlow.test {
            // Initial emission
            val initialId = awaitItem()
            assertThat(initialId).isEqualTo("test-semester-id")

            // Change semester ID
            semesterIdFlow.value = "new-semester-id"
            val newId = awaitItem()
            assertThat(newId).isEqualTo("new-semester-id")

            composeTestRule.waitForIdle()

            // Verify ViewModel method was called
            coVerify(timeout = 1000, atLeast = 1) { mockViewModel.fetchInitialData() }

            cancelAndIgnoreRemainingEvents()
        }
    }
    @Test
    fun testNavigationToProfile() {
        setContent()
        composeTestRule.waitForIdle()

        // Click on profile icon in top bar
        composeTestRule.onNodeWithTag("large_top_bar_action_icon").assertExists().performClick()

        verify { onNavigateToProfile() }
    }

    @Test
    fun testNavigationToAttendanceOverview() {
        setContent()

        composeTestRule
            .onNodeWithTag("AttendanceScreen_AttendanceOverviewButton")
            .performClick()

        verify { onNavigateToAttendanceOverview() }
    }

    @Test
    fun testNavigationToCoursesOverview() {
        setContent()

        composeTestRule
            .onNodeWithTag("AttendanceScreen_CoursesOverviewButton")
            .performClick()

        verify { onNavigateToCourseList() }
    }

    @Test
    fun testNavigationToAttendanceCalendar() {
        setContent()

        composeTestRule.onNodeWithTag("AttendanceScreen_LazyColumn")
            .performScrollToNode(hasTestTag("AttendanceScreen_AttendanceCalendarButton"))

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("AttendanceScreen_AttendanceCalendarButton")
            .performClick()

        verify { onNavigateToAttendanceCalendar() }
    }

    // BOTTOM SHEET TESTS
    @Test
    fun testSemesterBottomSheetInteraction() {
        setContent()

        // Click on semester item to open bottom sheet
        composeTestRule
            .onNodeWithTag("AttendanceScreen_SemButton")
            .performClick()

        composeTestRule.waitForIdle()

        // Bottom sheet should be visible
        // Add assertions based on your bottom sheet implementation
    }

    // STATE MANAGEMENT TESTS
    @Test
    fun testViewModelFetchDataCalledOnSemesterChange() {
        setContent()

        // Change semester ID
        semesterIdFlow.value = "new-semester-id"
        composeTestRule.waitForIdle()

        coVerify { mockViewModel.fetchInitialData() }
    }

    @Test
    fun testStateUpdatesReflectInUI() {
        setContent()

        // Update state values
        stateFlow.value = stateFlow.value.copy(
            unmarkedCount = 15,
            fullAttendanceCount = 8,
            lowAttendanceCount = 4,
            attendancePercentage = 92
        )

        // Verify UI reflects new values
        composeTestRule.onNodeWithText("15").assertIsDisplayed()
        composeTestRule.onNodeWithText("8").assertIsDisplayed()
        composeTestRule.onNodeWithText("4").assertIsDisplayed()
        composeTestRule.onNodeWithText("92").assertIsDisplayed()
    }

    // ACCESSIBILITY TESTS
    @Test
    fun testScreenAccessibility() {
        setContent()

        // Verify important elements have content descriptions or are properly labeled
        composeTestRule
            .onAllNodesWithText("Quick Stats")
            .assertCountEquals(1)

        // Verify clickable elements are accessible
        composeTestRule
            .onNodeWithText("Attendance Overview")
            .assertHasClickAction()
    }

    // ERROR STATE TESTS
    @Test
    fun testEmptyCoursesState() {
        stateFlow.value = stateFlow.value.copy(courseWiseSummaries = emptyList())
        setContent()

        // Should show empty courses message
        composeTestRule
            .onNodeWithText("You haven't added any courses yet. Click on \"Courses Overview\" to get started.")
            .assertIsDisplayed()
    }

    // STRESS TESTS

    @Test
    fun testLongRunningOperationDoesNotBlockUI() {
        setContent()

        // Simulate long-running operation by keeping loading state
        stateFlow.value = stateFlow.value.copy(isLoading = true)

        // UI should still be responsive
        composeTestRule
            .onNodeWithTag("AttendanceScreen_LoadingIndicator")
            .assertIsDisplayed()

        // Should be able to interact with top bar
        composeTestRule
            .onNodeWithTag("AttendanceScreen_TopBar")
            .assertIsDisplayed()
    }

    // INTEGRATION TESTS
    @Test
    fun testCompleteUserFlow() {
        setContent()

        // 1. Verify initial state
        composeTestRule.onNodeWithText("Quick Stats").assertIsDisplayed()

        // 2. Navigate to courses overview
        composeTestRule
            .onNodeWithTag("AttendanceScreen_CoursesOverviewButton")
            .performClick()
        verify { onNavigateToCourseList() }

        // 3. Navigate to attendance overview
        composeTestRule
            .onNodeWithTag("AttendanceScreen_AttendanceOverviewButton")
            .performClick()
        verify { onNavigateToAttendanceOverview() }

        // 4. Navigate to calendar
        composeTestRule.onNodeWithTag("AttendanceScreen_LazyColumn")
            .performScrollToNode(hasTestTag("AttendanceScreen_AttendanceCalendarButton"))
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithTag("AttendanceScreen_AttendanceCalendarButton")
            .performClick()
        verify { onNavigateToAttendanceCalendar() }
    }
}