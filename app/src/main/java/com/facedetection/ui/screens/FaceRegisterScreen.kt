package com.facedetection.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.facedetection.ui.components.ExitAlertDialog
import com.facedetection.ui.components.FaceRegistrationUI

@Composable
fun FaceRegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: FaceRegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    FaceRegistrationUI(
        uiState = uiState,
        onCapture = { face, faceStatus ->
            viewModel.processCapturedImage(faceStatus, face)
        },
        onSubmit = { viewModel.submitFaceData() },
        onClose = { showExitDialog = true }
    )

    if (showExitDialog) {
        ExitAlertDialog(
            title = "Exit Face Registration",
            message = "Are you sure you want to exit? All progress will be lost.",
            onConfirm = {
                showExitDialog = false
                (context as? Activity)?.finish()
            },
            onDismiss = { showExitDialog = false }
        )
    }
}
