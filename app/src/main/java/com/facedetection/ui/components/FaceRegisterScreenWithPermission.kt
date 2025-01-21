package com.facedetection.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.facedetection.ui.screens.FaceRegisterScreen

@Composable
fun FaceRegisterScreenWithPermission() {
    val context = LocalContext.current
    val permissionState = remember { mutableStateOf(false) }

    // Permission Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionState.value = isGranted
    }

    // Check permission and handle UI
    if (permissionState.value || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Permission granted - Show the face registration screen
        FaceRegisterScreen()
    } else {
        // Permission not granted - Show UI to request permission
        RequestPermissionUI(onRequestPermission = { launcher.launch(Manifest.permission.CAMERA) })
    }
}

