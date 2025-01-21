package com.facedetection.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.facedetection.ui.screens.FaceRegisterUiState
import com.facedetection.ui.screens.FaceStatus
import com.google.mlkit.vision.face.Face

@Composable
fun FaceRegistrationUI(
    uiState: FaceRegisterUiState,
    onCapture: (Face, FaceStatus) -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E88E5))
    ) {
        // Close button at the top right
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Camera Preview with Face Detection
            CameraPreviewWithFaceDetection(
                onFaceDetected = { face, faceStatus ->
                    onCapture(face, faceStatus)
                },
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Instruction Text
            Text(
                text = when (uiState.faceStatus) {
                    FaceStatus.CAPTURING_CENTER -> "Keep your head straight"
                    FaceStatus.CAPTURING_LEFT -> "Turn your head slightly to the left"
                    FaceStatus.CAPTURING_RIGHT -> "Turn your head slightly to the right"
                    FaceStatus.COMPLETED -> "Face capture completed"
                    else -> "Align your face within the circle"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Capture Buttons and Submit
            FaceRegistrationControls(
                uiState = uiState,
                onSubmit = onSubmit
            )
        }
    }
}
