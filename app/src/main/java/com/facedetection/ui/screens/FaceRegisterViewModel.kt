package com.facedetection.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facedetection.data.repository.FaceDetectionRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceRegisterViewModel @Inject constructor(
    private val repository: FaceDetectionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FaceRegisterUiState())
    val uiState: StateFlow<FaceRegisterUiState> = _uiState

    private var centerFace: Face? = null
    private var leftFace: Face? = null
    private var rightFace: Face? = null

    fun processCapturedImage(image: InputImage) {
        viewModelScope.launch {
            val detectedFace = repository.detectFace(image)
            if (detectedFace != null) {
                when (_uiState.value.faceStatus) {
                    FaceStatus.CAPTURING_CENTER -> {
                        centerFace = detectedFace
                        _uiState.update { it.copy(centerCaptured = true, faceStatus = FaceStatus.CAPTURING_LEFT) }
                    }
                    FaceStatus.CAPTURING_LEFT -> {
                        if (repository.validateFaceMatch(centerFace!!, detectedFace)) {
                            leftFace = detectedFace
                            _uiState.update { it.copy(leftCaptured = true, faceStatus = FaceStatus.CAPTURING_RIGHT) }
                        } else {
                            _uiState.update { it.copy(errorMessage = "Faces don't match, please adjust.") }
                        }
                    }
                    FaceStatus.CAPTURING_RIGHT -> {
                        if (repository.validateFaceMatch(centerFace!!, detectedFace)) {
                            rightFace = detectedFace
                            _uiState.update { it.copy(rightCaptured = true, faceStatus = FaceStatus.COMPLETED) }
                        } else {
                            _uiState.update { it.copy(errorMessage = "Faces don't match, please adjust.") }
                        }
                    }
                    else -> Unit
                }
            } else {
                _uiState.update { it.copy(errorMessage = "No face detected. Please try again.") }
            }
        }
    }

    fun submitFaceData() {
        if (_uiState.value.faceStatus == FaceStatus.COMPLETED) {
            // Submit the registered face data
            // Add necessary logic to store or send face data to repository
        }
    }
}