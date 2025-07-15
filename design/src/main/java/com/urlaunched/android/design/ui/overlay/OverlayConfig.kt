package com.urlaunched.android.design.ui.overlay

import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize

data class OverlayConfig(
    val offset: DpOffset = DpOffset.Zero,
    val backgroundAlpha: Float = 0.5f,
    val usePlatformDefaultWidth: Boolean = false,
    val dismissOnClickOutside: Boolean = true,
    val dismissOnBackPress: Boolean = true,
    val windowAnimationsRes: Int? = null,
    val widthOverride: Int? = null,
    val overlaySize: IntSize? = null,
)