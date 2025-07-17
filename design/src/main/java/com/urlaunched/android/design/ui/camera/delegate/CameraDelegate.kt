package com.urlaunched.android.design.ui.camera.delegate

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.urlaunched.android.design.ui.camera.model.CameraModeTypePresentationModel
import com.urlaunched.android.design.ui.camera.model.RecordingStatePresentationModel
import com.urlaunched.android.design.ui.camera.delegate.CameraDelegateImpl.CameraSideEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CameraDelegate {
    val uiCameraState: StateFlow<CameraDelegateState>
    val cameraSideEffect: Flow<CameraSideEffect>

    fun bindPreviewView(previewView: PreviewView)
    fun bindLifecycle(lifecycleOwner: LifecycleOwner)
    fun unbindLifecycle()

    fun toggleCameraType()
    fun toggleFlash()

    fun onPhotoCameraClick()
    fun onVideoCameraClick()
    fun onGalleryClick()

    fun onShutterClick(onCaptureMedia: (uri: Uri) -> Unit)
    fun onPause()

    fun onMediaPick(uris: List<Uri>, onHandleUris: (uris: List<Uri>) -> Unit)
}