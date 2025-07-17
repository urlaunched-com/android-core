package com.urlaunched.android.design.ui.camera.delegate

import android.Manifest
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraUnavailableException
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.CameraController.VIDEO_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.concurrent.futures.await
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import com.urlaunched.android.design.ui.camera.model.CameraModeTypePresentationModel
import com.urlaunched.android.design.ui.camera.model.RecordingStatePresentationModel
import com.urlaunched.android.design.ui.camera.util.VideoRecordingTempFileProvider
import com.urlaunched.android.design.ui.camera.util.PhotoTempFileProvider
import com.urlaunched.android.design.ui.textfield.hashtags.Delegate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class CameraDelegateImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher,
    private val lifecycleCameraController: LifecycleCameraController,
    private val videoRecordingTempFileProvider: VideoRecordingTempFileProvider,
    private val photoTempFileProvider: PhotoTempFileProvider,
    private val viewModelScope: CoroutineScope,
) : Delegate(coroutineDispatcher = ioDispatcher), CameraDelegate {
    private val _uiState = MutableStateFlow(
        CameraDelegateState(
            cameraType = CameraType.BACK,
            recordingState = RecordingStatePresentationModel.Stopped,
            cameraMode = CameraModeTypePresentationModel.VIDEO,
            isFlashEnabled = true,
            shouldRestoreStatusBarColors = false,
        )
    )
    private val _cameraSideEffect = Channel<CameraSideEffect>()
    override val cameraSideEffect: Flow<CameraSideEffect> get() = _cameraSideEffect.receiveAsFlow()

    override val uiCameraState: StateFlow<CameraDelegateState> = _uiState

    private var isCameraInitialized = false
    private var recordController: androidx.camera.video.Recording? = null

    init {
        lifecycleCameraController.setEnabledUseCases(VIDEO_CAPTURE or IMAGE_CAPTURE)
        initCameraController()
    }

    private fun initCameraController() {
        viewModelScope.launch(mainDispatcher) {
            try {
                lifecycleCameraController.initializationFuture.await()
                isCameraInitialized = true
            } catch (e: CameraUnavailableException) {
                withContext(ioDispatcher) {
                    _cameraSideEffect.send(CameraSideEffect.ShowCameraInitErrorSnackbar)
                }
            }
        }
    }

    override fun bindPreviewView(previewView: PreviewView) {
        previewView.controller = lifecycleCameraController
    }

    override fun bindLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleCameraController.bindToLifecycle(lifecycleOwner)
    }

    override fun unbindLifecycle() {
        lifecycleCameraController.unbind()
    }

    override fun toggleCameraType() {
        val newType = if (_uiState.value.cameraType == CameraType.BACK) CameraType.FRONT else CameraType.BACK
        setCameraType(newType)
    }

    private fun setCameraType(cameraType: CameraType) {
        if (_uiState.value.cameraType == cameraType) return
        if (hasCamera(cameraType) && !lifecycleCameraController.isRecording) {
            if (lifecycleCameraController.cameraSelector != cameraType.selector) {
                lifecycleCameraController.cameraSelector = cameraType.selector
                _uiState.update { it.copy(cameraType = cameraType) }
            }
        }
    }

    private fun hasCamera(cameraType: CameraType): Boolean =
        isCameraInitialized && lifecycleCameraController.hasCamera(cameraType.selector)

    override fun toggleFlash() {
        val newFlash = !_uiState.value.isFlashEnabled
        lifecycleCameraController.imageCaptureFlashMode = if (newFlash) FLASH_MODE_AUTO else FLASH_MODE_OFF
        _uiState.update { it.copy(isFlashEnabled = newFlash) }
    }

    override fun onPhotoCameraClick() {
        _uiState.update { it.copy(cameraMode = CameraModeTypePresentationModel.PHOTO) }
    }

    override fun onVideoCameraClick() {
        _uiState.update { it.copy(cameraMode = CameraModeTypePresentationModel.VIDEO) }
    }

    override fun onGalleryClick() {
        viewModelScope.launch { _cameraSideEffect.send(CameraSideEffect.PickGalleryMedia) }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun onShutterClick(onCaptureMedia: (uri: Uri) -> Unit) {
        when (_uiState.value.cameraMode) {
            CameraModeTypePresentationModel.PHOTO -> takePicture(onCaptureMedia)
            CameraModeTypePresentationModel.VIDEO -> toggleRecording(onCaptureMedia)
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun toggleRecording(onCaptureMedia: (uri: Uri) -> Unit) {
        when (_uiState.value.recordingState) {
            is RecordingStatePresentationModel.Recording -> stopRecording()
            is RecordingStatePresentationModel.Stopped -> startRecording(90.seconds, onCaptureMedia)
        }
    }

    private fun takePicture(onCaptureMedia: (uri: Uri) -> Unit) {
        lifecycleCameraController.takePicture(
            ImageCapture.OutputFileOptions.Builder(photoTempFileProvider()).build(),
            Runnable::run,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    showSnackbar(error.message.orEmpty())
                }

                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    result.savedUri?.let { uri ->
                        onCaptureMedia(uri)

                    }
                }
            }
        )
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording(limit: Duration, onCaptureMedia: (uri: Uri) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            val file = videoRecordingTempFileProvider()
            withContext(mainDispatcher) {
                try {
                    recordController = lifecycleCameraController.startRecording(
                        FileOutputOptions.Builder(file)
                            .setDurationLimitMillis(limit.inWholeMilliseconds)
                            .build(),
                        AudioConfig.create(true),
                        Runnable::run,
                        withContext(mainDispatcher) {
                            recordingStateListener(limit, onCaptureMedia)
                        }
                    )
                } catch (e: Exception) {
                    _uiState.update { it.copy(recordingState = RecordingStatePresentationModel.Stopped) }
                    showSnackbar(e.message.orEmpty())
                }
            }
        }
    }

    private fun stopRecording() {
        recordController?.stop()
    }

    private fun recordingStateListener(
        recordingDurationLimit: Duration,
        onCaptureMedia: (uri: Uri) -> Unit
    ): Consumer<VideoRecordEvent> =
        Consumer { event ->
            viewModelScope.launch(ioDispatcher) {
                when (event) {
                    is VideoRecordEvent.Finalize -> {
                        _uiState.update { uiState ->
                            uiState.copy(
                                recordingState = RecordingStatePresentationModel.Stopped
                            )
                        }

                        when {
                            !event.hasError() || event.error == VideoRecordEvent.Finalize.ERROR_DURATION_LIMIT_REACHED -> {
                                withContext(mainDispatcher) {
                                    onCaptureMedia(event.outputResults.outputUri)
                                }
                            }

                            else -> showSnackbar(event.cause?.message.toString())
                        }

                        recordController = null
                    }

                    else -> {
                        _uiState.update { uiState ->
                            uiState.copy(
                                recordingState = RecordingStatePresentationModel.Recording(
                                    event.recordingStats.recordedDurationNanos.nanoseconds,
                                    recordingDurationLimit
                                )
                            )
                        }
                    }
                }
            }
        }

    override fun onPause() {
        recordController?.stop()
    }

    override fun onMediaPick(uris: List<Uri>, onHandleUris: (uris: List<Uri>) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(shouldRestoreStatusBarColors = true) }
            onHandleUris(uris)
        }
    }

    private fun showSnackbar(msg: String) {
        viewModelScope.launch(ioDispatcher) {
            _cameraSideEffect.send(CameraSideEffect.ShowSnackbar(msg))
        }
    }

    sealed class CameraSideEffect {
        data class ShowSnackbar(val message: String) : CameraSideEffect()
        data object ShowCameraInitErrorSnackbar : CameraSideEffect()
        data object PickGalleryMedia : CameraSideEffect()
    }
}