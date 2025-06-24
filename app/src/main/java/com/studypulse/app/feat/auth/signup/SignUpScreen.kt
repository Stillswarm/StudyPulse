package com.studypulse.app.feat.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: () -> Unit,
    navigateToHome: () -> Unit,
    vm: SignUpScreenViewModel = koinViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Blue)
        )

        Text(
            text = "StudyPulse",
            style = MaterialTheme.typography.displaySmall,
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
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
                focusedContainerColor = Color.LightGray,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            onValueChange = { vm.updateEmail(it) },
            label = { Text("Email") }
        )

        TextField(
            value = state.password,
            onValueChange = { vm.updatePassword(it) },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            isError = state.passwordError,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
                focusedContainerColor = Color.LightGray,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
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
            onClick = {
                vm.signUp(navigateToHome)
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

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