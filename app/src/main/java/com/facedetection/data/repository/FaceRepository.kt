package com.facedetection.data.repository

import com.google.mlkit.vision.face.Face

interface FaceRepository {
    suspend fun saveFaceData(centerFace: Face, leftFace: Face, rightFace: Face)
    suspend fun submitFaceData(): Result<String>
}