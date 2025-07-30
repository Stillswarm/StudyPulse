package com.studypulse.app.feat.auth.signup

import androidx.activity.compose.LocalActivity
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: () -> Unit,
    vm: SignUpScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current

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
            text = "Your Academic Journey Starts Here",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            value = state.email,
            isError = state.emailError,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LightGray.copy(alpha = 0.2f),
                focusedContainerColor = LightGray,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedLabelColor = DarkGray,
            ),
            singleLine = true,
            onValueChange = { vm.updateEmail(it) },
            label = { Text("Enter your email address") }
        )

        TextField(
            value = state.password,
            onValueChange = { vm.updatePassword(it) },
            label = { Text("Create your password") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            isError = state.passwordError,
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

        state.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = { vm.signUp(activity) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
        ) {
            Text(
                text = "Sign Up",
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
                    vm.handleGoogleSignIn(activity, context)
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
                    text = "Sign up with Google",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = buildAnnotatedString {
                append("Already have an account? ")
                val clickableAnnotation = LinkAnnotation.Clickable("sign_in") {
                    navigateToSignIn()
                }
                withLink(clickableAnnotation) {
                    withStyle(
                        SpanStyle(
                            color = Color.Blue,
                        )
                    ) {
                        append("Sign In")
                    }
                }
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}