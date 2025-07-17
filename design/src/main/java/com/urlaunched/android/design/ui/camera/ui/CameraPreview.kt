package com.urlaunched.android.design.ui.camera.ui

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal fun CameraPreview(modifier: Modifier = Modifier, onCameraPreviewViewAvailable: (view: PreviewView) -> Unit) {
    if (!LocalInspectionMode.current) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    onCameraPreviewViewAvailable(this)

                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            onRelease = { view ->
                view.controller = null
            }
        )
    } else {
        Box(modifier.background(color = Color(0xFF4CAF50)))
    }
}