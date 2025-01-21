package com.facedetection.data.repository

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FaceDetectionRepository @Inject constructor() {
    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
//            .enableTracking()
            .build()
    )

    suspend fun detectFace(image: InputImage): Face? {
        return try {
            val result = detector.process(image).await()
            result.firstOrNull()
        } catch (e: Exception) {
            Log.e("FaceDetectionRepository", "Error detecting face: ${e.message}")
            null
        }
    }

    fun validateFaceMatch(face1: Face, face2: Face): Boolean {
        val leftEye1 = face1.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val leftEye2 = face2.getLandmark(FaceLandmark.LEFT_EYE)?.position

        val rightEye1 = face1.getLandmark(FaceLandmark.RIGHT_EYE)?.position
        val rightEye2 = face2.getLandmark(FaceLandmark.RIGHT_EYE)?.position

        val noseBase1 = face1.getLandmark(FaceLandmark.NOSE_BASE)?.position
        val noseBase2 = face2.getLandmark(FaceLandmark.NOSE_BASE)?.position

        return leftEye1 == leftEye2 && rightEye1 == rightEye2 && noseBase1 == noseBase2
    }
}