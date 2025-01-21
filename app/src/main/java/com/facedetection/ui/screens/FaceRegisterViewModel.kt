package com.facedetection.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceRegisterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(FaceRegisterUiState())
    val uiState: StateFlow<FaceRegisterUiState> = _uiState

    private var centerFace: Face? = null
    private var leftFace: Face? = null
    private var rightFace: Face? = null

    fun processCapturedImage(faceStatus: FaceStatus, face: Face) {
        viewModelScope.launch {
            when (faceStatus) {
                FaceStatus.CAPTURING_CENTER -> {
                    if (!_uiState.value.centerCaptured) {
                        centerFace = face
                        _uiState.update {
                            it.copy(
                                centerCaptured = true,
                                faceStatus = FaceStatus.CAPTURING_LEFT
                            )
                        }
                    }
                }

                FaceStatus.CAPTURING_LEFT -> {
                    if (_uiState.value.centerCaptured && !_uiState.value.leftCaptured) {
                        if (validateFaceMatch(centerFace!!, face)) {
                            leftFace = face
                            _uiState.update {
                                it.copy(
                                    leftCaptured = true,
                                    faceStatus = FaceStatus.CAPTURING_RIGHT
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(errorMessage = "Left face doesn't match the center face. Please try again.")
                            }
                        }
                    }
                }

                FaceStatus.CAPTURING_RIGHT -> {
                    if (_uiState.value.centerCaptured && _uiState.value.leftCaptured && !_uiState.value.rightCaptured) {
                        if (validateFaceMatch(centerFace!!, face)) {
                            rightFace = face
                            _uiState.update {
                                it.copy(
                                    rightCaptured = true,
                                    faceStatus = FaceStatus.COMPLETED
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(errorMessage = "Right face doesn't match the center face. Please try again.")
                            }
                        }
                    }
                }

                else -> {
                    _uiState.update {
                        it.copy(errorMessage = "Unexpected face position. Please align correctly.")
                    }
                }
            }
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

    fun submitFaceData() {
        if (_uiState.value.faceStatus == FaceStatus.COMPLETED) {
            // Submit the registered face data
            // Add necessary logic to store or send face data to repository
        }
    }
}