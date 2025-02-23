package com.facedetection.ui.screens

data class FaceRegisterUiState(
    val faceStatus: FaceStatus = FaceStatus.DETECTING,
    val centerCaptured: Boolean = false,
    val leftCaptured: Boolean = false,
    val rightCaptured: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
