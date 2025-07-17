package com.urlaunched.android.design.ui.camera.delegate

import com.urlaunched.android.design.ui.camera.model.CameraModeTypePresentationModel
import com.urlaunched.android.design.ui.camera.model.RecordingStatePresentationModel

data class CameraDelegateState(
    val cameraType: CameraType,
    val recordingState: RecordingStatePresentationModel,
    val cameraMode: CameraModeTypePresentationModel,
    val isFlashEnabled: Boolean,
    val shouldRestoreStatusBarColors: Boolean,
)