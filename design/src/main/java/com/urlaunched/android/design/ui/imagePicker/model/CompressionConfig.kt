package com.urlaunched.android.design.ui.imagepicker.model

import com.urlaunched.android.design.ui.imagepicker.dimens.CompressionConstants

data class CompressionConfig(
    val minWidth: Int = CompressionConstants.DEFAULT_MIN_WIDTH,
    val minHeight: Int = CompressionConstants.DEFAULT_MIN_HEIGHT,
    val targetSizeBytes: Long = CompressionConstants.DEFAULT_TARGET_SIZE_BYTES,
    val maxSizeBytes: Long = CompressionConstants.DEFAULT_MAX_SIZE_BYTES
)

