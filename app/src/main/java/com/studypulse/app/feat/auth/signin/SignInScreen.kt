package com.studypulse.app.feat.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.OrDivider
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.LightGray
import com.studypulse.app.ui.theme.Red
import com.studypulse.app.ui.theme.WhiteSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navigateToSignUp: () -> Unit,
    vm: SignInScreenViewModel = koinViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val forgotPasswordBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.emailSent) {
        if (state.emailSent) {
            while (state.counter > 0) {
                delay(1000)
                vm.decrementCounter()
            }
            vm.resetEmailSent()
        }
    }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_pulse),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            colorFilter = ColorFilter.lighting(Gold, Color.Black)
        )

        Text(
            text = "StudyPulse",
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            value = state.email,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LightGray.copy(alpha = 0.2f),
                focusedContainerColor = LightGray,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedLabelColor = DarkGray,
            ),
            singleLine = true,
            onValueChange = { vm.updateEmail(it) },
            placeholder = { Text("Enter your email address", fontSize = 14.sp) },
//            label = { Text("Enter your email address", fontSize = 12.sp) },

        )

        TextField(
            value = state.password,
            onValueChange = { vm.updatePassword(it) },
//            label = { Text("Enter correct password", fontSize = 14.sp) },
            placeholder = { Text("Enter correct password", fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LightGray.copy(alpha = 0.2f),
                focusedContainerColor = LightGray,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedLabelColor = DarkGray,
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Text(
            text = if (state.emailSent) "Email sent successfully" else "Forgot Password?",
            color = Color.Blue,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    vm.updateBottomSheetEmail(state.email)
                    scope.launch { forgotPasswordBottomSheetState.show() }
                }
        )

        state.error?.let { error ->
            Text(
                text = error,
                color = Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = { vm.signIn() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
        ) {
            Text(
                text = "Sign In",
                fontSize = 16.sp,
                color = Color.White,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
        OrDivider()
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(
                    1.dp, DarkGray,
                    RoundedCornerShape(8.dp)
                )
                .noRippleClickable {
                    vm.handleGoogleSignIn(context)
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_google),
                    contentDescription = "Google",
                )

                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Sign in with Google",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = buildAnnotatedString {
                append("Don't have an account? ")
                val clickableAnnotation = LinkAnnotation.Clickable("sign_in") {
                    navigateToSignUp()
                }
                withLink(clickableAnnotation) {
                    withStyle(
                        SpanStyle(
                            color = Color.Blue,
                        )
                    ) {
                        append("Sign Up")
                    }
                }
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (forgotPasswordBottomSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch { forgotPasswordBottomSheetState.hide() } },
            sheetState = forgotPasswordBottomSheetState,
            shape = RectangleShape,
            containerColor = WhiteSecondary,
            dragHandle = {}
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    value = state.bottomSheetEmail,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = LightGray.copy(alpha = 0.2f),
                        focusedContainerColor = LightGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = DarkGray,
                    ),
                    singleLine = true,
                    onValueChange = { vm.updateBottomSheetEmail(it) },
                    placeholder = { Text("Enter your email address", fontSize = 14.sp) },
//                    label = { Text("Enter your email address") }
                )

                state.error?.let { error ->
                    Text(
                        text = error,
                        color = Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = { vm.sendPasswordResetEmail() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !state.emailSent,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                ) {
                    Text(
                        text = if (state.emailSent) "Retry in ${state.counter} seconds" else "Send Recovery Email",
                        fontSize = 16.sp,
                        color = Color.White,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}