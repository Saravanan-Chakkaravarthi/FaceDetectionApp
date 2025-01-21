package com.facedetection.ui.components

import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.facedetection.ui.screens.FaceStatus
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

@Composable
fun CameraPreviewWithFaceDetection(
    onFaceDetected: (Face, FaceStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                processImageProxy(imageProxy, onFaceDetected)
            }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            try {
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Failed to bind camera use cases", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    onFaceDetected: (Face, FaceStatus) -> Unit
) {
    val mediaImage = imageProxy.image ?: return
    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
    )

    detector.process(inputImage)
        .addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) {
                val face = faces.first()
                val faceStatus = getFaceStatus(face.boundingBox)
                onFaceDetected(face, faceStatus)
            }
        }
        .addOnFailureListener { Log.e("FaceDetection", "Error detecting face", it) }
        .addOnCompleteListener { imageProxy.close() }
}

private fun getFaceStatus(boundingBox: Rect): FaceStatus {
    val faceCenterX = boundingBox.centerX()
    val imageWidth = 300
    val imageCenterX = imageWidth / 2
    return when {
        faceCenterX < imageCenterX - (imageWidth / 4) -> FaceStatus.CAPTURING_LEFT
        faceCenterX > imageCenterX + (imageWidth / 4) -> FaceStatus.CAPTURING_RIGHT
        else -> FaceStatus.CAPTURING_CENTER
    }
}


