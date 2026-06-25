package com.techquote.app.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Stable
class DemoFeedbackController(
    val snackbarHostState: SnackbarHostState,
    val showMessage: (String) -> Unit,
)

@Composable
fun rememberDemoFeedback(): DemoFeedbackController {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    return remember(snackbarHostState, coroutineScope) {
        DemoFeedbackController(
            snackbarHostState = snackbarHostState,
            showMessage = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            },
        )
    }
}
