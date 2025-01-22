package com.facedetection.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facedetection.data.repository.FaceRepository
import com.google.mlkit.vision.face.Face
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceRegisterViewModel @Inject constructor(
    private val repository: FaceRepository
) : ViewModel() {
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
                        leftFace = face
                        _uiState.update {
                            it.copy(
                                leftCaptured = true,
                                faceStatus = FaceStatus.CAPTURING_RIGHT
                            )
                        }
                    }
                }

                FaceStatus.CAPTURING_RIGHT -> {
                    if (!_uiState.value.rightCaptured && _uiState.value.leftCaptured) {
                        rightFace = face
                        _uiState.update {
                            it.copy(
                                rightCaptured = true,
                                faceStatus = FaceStatus.COMPLETED
                            )
                        }
                    } else if (_uiState.value.centerCaptured && _uiState.value.leftCaptured && _uiState.value.rightCaptured) {
                        _uiState.update {
                            it.copy(errorMessage = "Please recenter your face.")
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

    fun submitFaceData() {
        viewModelScope.launch {
            if (_uiState.value.faceStatus == FaceStatus.COMPLETED) {
                centerFace?.let { center ->
                    leftFace?.let { left ->
                        rightFace?.let { right ->
                            repository.saveFaceData(center, left, right)
                            val result = repository.submitFaceData()
                            result.onSuccess {
                                _uiState.update {
                                    it.copy(successMessage = it.successMessage)
                                }
                            }.onFailure {
                                _uiState.update {
                                    it.copy(errorMessage = it.errorMessage)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}