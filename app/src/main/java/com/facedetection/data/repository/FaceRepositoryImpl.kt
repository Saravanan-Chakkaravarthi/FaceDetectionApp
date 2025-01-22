package com.facedetection.data.repository

import android.util.Log
import com.google.mlkit.vision.face.Face
import javax.inject.Inject

class FaceRepositoryImpl @Inject constructor() : FaceRepository {

    override suspend fun saveFaceData(centerFace: Face, leftFace: Face, rightFace: Face) {
        Log.d("FaceRepository", "Saving face data: $centerFace, $leftFace, $rightFace")
    }

    override suspend fun submitFaceData(): Result<String> {
        return try {
            Result.success("Face data submitted successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
