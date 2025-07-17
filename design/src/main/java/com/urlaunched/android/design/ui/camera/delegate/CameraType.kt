package com.urlaunched.android.design.ui.camera.delegate

import androidx.camera.core.CameraSelector

enum class CameraType(
    val selector: CameraSelector
) {
    FRONT(CameraSelector.DEFAULT_FRONT_CAMERA),
    BACK(CameraSelector.DEFAULT_BACK_CAMERA)
}