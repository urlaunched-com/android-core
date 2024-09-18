package com.urlaunched.android.common.firebaseperformance

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.application.FrameMetricsRecorder

@Composable
fun LogFirebasePerformance(route: String) {
    val activity = LocalContext.current as Activity
    val trace = FirebasePerformance.getInstance().newTrace(route).apply { start() }

    DisposableEffect(route) {
        val recorder = FrameMetricsRecorder(activity).apply { start() }

        onDispose {
            val metrics = recorder.stop()

            if (metrics.isAvailable) {
                val frameMetrics = metrics.get()
                trace.putMetric(FROZEN_FRAMES_TAG, frameMetrics.frozenFrames.toLong())
                trace.putMetric(SLOW_FRAMES_TAG, frameMetrics.slowFrames.toLong())
                trace.putMetric(TOTAL_FRAMES_TAG, frameMetrics.totalFrames.toLong())
            }

            trace.stop()
        }
    }
}

const val FROZEN_FRAMES_TAG = "frozen_frames"
const val SLOW_FRAMES_TAG = "slow_frames"
const val TOTAL_FRAMES_TAG = "total_frames"