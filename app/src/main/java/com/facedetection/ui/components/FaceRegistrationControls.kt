package com.facedetection.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facedetection.ui.screens.FaceRegisterUiState
import com.facedetection.ui.screens.FaceStatus

@Composable
fun FaceRegistrationControls(
    uiState: FaceRegisterUiState,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CaptureButton(label = "Center", isCaptured = uiState.centerCaptured)
        Spacer(modifier = Modifier.width(16.dp))
        CaptureButton(label = "Left", isCaptured = uiState.leftCaptured)
        Spacer(modifier = Modifier.width(16.dp))
        CaptureButton(label = "Right", isCaptured = uiState.rightCaptured)
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = onSubmit,
        enabled = uiState.faceStatus == FaceStatus.COMPLETED,
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Text(text = "Submit")
    }
}
