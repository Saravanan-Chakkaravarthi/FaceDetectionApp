package com.facedetection.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.facedetection.R
import com.facedetection.ui.components.CameraPreview

@Composable
fun FaceRegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: FaceRegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Blue),
    ) {
        // Camera Preview in the Center
        CameraPreview(
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp), // Adjust size as needed
            onImageCaptured = { image ->
                viewModel.processCapturedImage(image)
            }
        )

        // Bottom-Centered Column for UI Elements
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            // Face Status Message
            Text(
                text = when (uiState.faceStatus) {
                    FaceStatus.CAPTURING_CENTER -> "Align your face in the center"
                    FaceStatus.CAPTURING_LEFT -> "Turn your face left"
                    FaceStatus.CAPTURING_RIGHT -> "Turn your face right"
                    FaceStatus.COMPLETED -> "Face registration completed"
                    else -> uiState.errorMessage ?: "Detecting face..."
                },
                color = if (uiState.faceStatus == FaceStatus.COMPLETED) Color.Green else Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            // Display Face Previews (Center, Left, Right)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual drawable
                        contentDescription = "Center",
                        modifier = Modifier
                            .size(60.dp)
                            .border(
                                width = 1.dp,
                                color = if (uiState.faceStatus == FaceStatus.CAPTURING_CENTER) Color.Green else Color.LightGray,
                                shape = RectangleShape
                            )
                            .padding(4.dp)
                    )
                    Text(
                        text = "Center",
                        color = if (uiState.faceStatus == FaceStatus.COMPLETED) Color.Green else Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual drawable
                        contentDescription = "Left",
                        modifier = Modifier
                            .size(60.dp)
                            .border(
                                width = 1.dp,
                                color = if (uiState.faceStatus == FaceStatus.CAPTURING_LEFT) Color.Green else Color.LightGray,
                                shape = RectangleShape
                            )
                            .padding(4.dp)
                    )
                    Text(
                        text = "Left",
                        color = if (uiState.faceStatus == FaceStatus.COMPLETED) Color.Green else Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual drawable
                        contentDescription = "Right",
                        modifier = Modifier
                            .size(60.dp)
                            .border(
                                width = 1.dp,
                                color = if (uiState.faceStatus == FaceStatus.CAPTURING_RIGHT) Color.Green else Color.LightGray,
                                shape = RectangleShape
                            )
                            .padding(4.dp)
                    )
                    Text(
                        text = "Right",
                        color = if (uiState.faceStatus == FaceStatus.COMPLETED) Color.Green else Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Submit Button
            Button(
                onClick = { viewModel.submitFaceData() },
                enabled = uiState.faceStatus == FaceStatus.COMPLETED,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "Submit",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
