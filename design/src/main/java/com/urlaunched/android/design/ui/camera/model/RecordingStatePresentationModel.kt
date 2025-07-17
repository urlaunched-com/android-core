package com.urlaunched.android.design.ui.camera.model

import kotlin.time.Duration

sealed class RecordingStatePresentationModel {
    data class Recording(val duration: Duration, val durationLimit: Duration) : RecordingStatePresentationModel() {
        val progress = (duration / durationLimit).toFloat()
    }

    data object Stopped : RecordingStatePresentationModel()
}