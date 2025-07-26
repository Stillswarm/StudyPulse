package com.studypulse.app.feat.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.WhiteSecondary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FeedbackScreen(vm: FeedbackScreenViewModel = koinViewModel()) {
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppTopBar(
            backgroundColor = DarkGray,
            foregroundGradient = null,
            title = "Report Error/Feedback",
            navigationIcon = R.drawable.logo_pulse,
            onNavigationClick = { scope.launch { NavigationDrawerController.toggle() } },
            actionIcon = null,
            onActionClick = null,
            titleColor = WhiteSecondary
        )
        Text(text = "Report an issue or send feedback:", modifier = Modifier.padding(horizontal = 16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Your message") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            minLines = 8,
            maxLines = 12
        )

        Button(
            onClick = {
                vm.submitFeedback(text)
                text = ""
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGray),
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.End)
        ) {
            Text("Submit", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
