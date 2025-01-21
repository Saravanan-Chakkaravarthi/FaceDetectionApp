package com.facedetection.ui.components

import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptured: (InputImage) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val detectedFaces = remember { mutableStateListOf<Rect>() }

    Box(modifier = modifier
        .size(300.dp) // Adjust size as needed
        .background(color = Color.LightGray, shape = CircleShape)
        .clip(CircleShape)
    ) {
        AndroidView(
            factory = { androidViewContext ->
                val previewView = PreviewView(androidViewContext)
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().apply {
                    surfaceProvider = previewView.surfaceProvider
                }

                val imageCapture = ImageCapture.Builder().build()
                val imageAnalysis = ImageAnalysis.Builder().build().apply {
                    setAnalyzer(Executors.newCachedThreadPool()) { imageProxy ->
                        processImageProxy(imageProxy, onImageCaptured, detectedFaces)
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                try {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    // Handle exceptions for Camera setup failure
                    Log.e("CameraPreview", "Camera setup failed", e)
                }

                previewView
            },
            modifier = modifier.fillMaxSize()
        )
        // Overlay Canvas to draw the face bounding boxes
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            detectedFaces.forEach { rect ->
                drawRect(
                    color = Color.Red,
                    size = androidx.compose.ui.geometry.Size(rect.width().toFloat(), rect.height().toFloat()),
                    topLeft = androidx.compose.ui.geometry.Offset(rect.left.toFloat(), rect.top.toFloat()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    onImageCaptured: (InputImage) -> Unit,
    detectedFaces: MutableList<Rect>
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        Log.e("FaceRegister", "MediaImage is null.")
        imageProxy.close()
        return
    }

    try {
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val faceDetector = FaceDetection.getClient()

        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                detectedFaces.clear()
                if (faces.isNotEmpty()) {
                    val face = faces[0]
                    detectedFaces.add(face.boundingBox)
                    onImageCaptured(inputImage)
                } else {
                    Log.d("FaceRegister", "No faces detected.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FaceRegister", "Face detection failed: ${e.message}", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } catch (e: Exception) {
        Log.e("FaceRegister", "Unexpected error during face detection: ${e.message}", e)
        imageProxy.close()
    }
}

