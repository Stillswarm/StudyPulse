package com.studypulse.app.feat.auth.signin

import android.app.Application
import app.cash.turbine.test
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.studypulse.app.SnackbarController
import com.studypulse.app.feat.user.domain.UserRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInScreenViewModelTest {

    // test setup
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // dependencies and VM
    private lateinit var userRepository: UserRepository
    private lateinit var application: Application
    private lateinit var vm: SignInScreenViewModel

    // other required dependencies in method bodies
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var snackbarController: SnackbarController

    // required mock data
    private val validEmail = "test@example.com"
    private val validPassword = "password123"
    private val invalidEmail = "invalidEmail"
    private val invalidPassword = "123"

    @Before
    fun setUp() {

        Dispatchers.setMain(testDispatcher)

        userRepository = mockk(relaxed = true)
        application = mockk()
        vm = SignInScreenViewModel(userRepository, application)

        firebaseAuth = mockk()
        snackbarController = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ======== STATE UPDATE TESTS ========

    @Test
    fun `updateEmail - should update email and reset error state`() =
        testScope.runTest {
            vm.updateEmail(validEmail)

            vm.state.test {
                val state = awaitItem()
                assertThat(state.email).isEqualTo(validEmail)
                assertThat(state.error).isNull()
                assertThat(state.emailSent).isFalse()
                assertThat(state.counter).isEqualTo(SignInScreenViewModel.RESET_COOLDOWN)
            }
        }

    @Test
    fun `updateBottomSheetEmail - should update email and reset error state`() =
        testScope.runTest {
            vm.updateBottomSheetEmail(validEmail)

            vm.state.test {
                val state = awaitItem()
                assertThat(state.email).isEqualTo(validEmail)
                assertThat(state.error).isNull()
                assertThat(state.emailSent).isFalse()
                assertThat(state.counter).isEqualTo(SignInScreenViewModel.RESET_COOLDOWN)
            }
        }

    @Test
    fun `updatePassword - should update password and reset error state`() =
        testScope.runTest {
            vm.updatePassword(validPassword)

            vm.state.test {
                val state = awaitItem()
                assertThat(state.password).isEqualTo(validPassword)
                assertThat(state.error).isNull()
                assertThat(state.emailSent).isFalse()
                assertThat(state.counter).isEqualTo(SignInScreenViewModel.RESET_COOLDOWN)
            }
        }

    @Test
    fun `resetEmailSent - should set emailSent to false`() =
        testScope.runTest {
            vm.resetEmailSent()

            vm.state.test {
                val state = awaitItem()
                assertThat(state.emailSent).isFalse()
            }
        }

    @Test
    fun `decrementCounter - should decrement counter`() =
        testScope.runTest {
            vm.decrementCounter()

            vm.state.test {
                val state = awaitItem()
                assertThat(state.counter).isEqualTo(SignInScreenViewModel.RESET_COOLDOWN - 1)
            }
        }


    // =======  EMAIL / PASSWORD SIGN-IN ======
    @Test
    fun `signIn - should fail when email is empty`() =
        testScope.runTest {
            vm.updateEmail("")
            vm.updatePassword(validPassword)
            vm.signIn()

            vm.state.test {
                val state = awaitItem()
                assertThat(state.error).isEqualTo(SignInScreenViewModel.EMPTY_EMAIL_ERROR)
            }
        }

    @Test
    fun `signIn - should fail when password is empty`() =
        testScope.runTest {
            vm.updateEmail(validEmail)
            vm.updatePassword("")
            vm.signIn()

            vm.state.test {
                val state = awaitItem()
                assertThat(state.error).isEqualTo(SignInScreenViewModel.EMPTY_PASSWORD_ERROR)
            }
        }

    @Test
    fun `signIn - should fail when email is invalid`() =
        testScope.runTest {
            vm.updateEmail(invalidEmail)
            vm.updatePassword(validPassword)
            vm.signIn()

            vm.state.test {
                val state = awaitItem()
                assertThat(state.error).isEqualTo(SignInScreenViewModel.INVALID_EMAIL_ERROR)
            }
        }

    @Test
    fun `signIn - should handle authentication failure`() =
        testScope.runTest {
            val authTask = mockk<Task<AuthResult>>()
            every { authTask.isSuccessful } returns false
            every { authTask.exception } returns
                    Exception("Auth failed")
            every { authTask.addOnCompleteListener(any()) } answers {
                val listener = firstArg<OnCompleteListener<AuthResult>>()
                listener.onComplete(authTask)
                authTask
            }

            mockkStatic("com.google.firebase.auth.AuthKt")
            every { com.google.firebase.ktx.Firebase.auth } returns firebaseAuth
            every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns authTask

            vm.updateEmail(validEmail)
            vm.updatePassword(validPassword)

            vm.signIn()
            testDispatcher.scheduler.advanceUntilIdle()

            vm.state.test {
                val state = awaitItem()
                assertThat(state.error).isEqualTo("Auth failed")
            }
        }

    // ===== RESET PASSWORD =====

    @Test
    fun `sendPasswordResetEmail - should fail when invalid email`() =
        testScope.runTest {
            vm.updateBottomSheetEmail(invalidEmail)
            vm.sendPasswordResetEmail()

            vm.state.test {
                val state = awaitItem()
                assertThat(state.error).isEqualTo(SignInScreenViewModel.INVALID_EMAIL_ERROR)
            }
        }

    @Test
    fun `sendPasswordResetEmail - should succeed when valid email and show snackbar`() =
        testScope.runTest {
            val resetTask = mockk<Task<Void>>()
            every { resetTask.isSuccessful } returns true
            every { resetTask.addOnCompleteListener(any()) } answers {
                val listener = firstArg<OnCompleteListener<Void>>()
                listener.onComplete(resetTask)
                resetTask
            }

            mockkStatic("com.google.firebase.auth.ktx.AuthKt")
            every { Firebase.auth } returns firebaseAuth
            every { firebaseAuth.sendPasswordResetEmail(any()) } returns resetTask
            coEvery { SnackbarController.sendEvent(any()) } just Runs
        }
}