package com.urlaunched.android.design.ui.camera.util

import com.urlaunched.android.design.ui.camera.model.AppMediaType

fun String.mimeToMediaType() = when {
    startsWith("image/") -> {
        AppMediaType.IMAGE_PREVIEW
    }

    startsWith("video/") -> {
        AppMediaType.VIDEO
    }

    else -> {
        null
    }
}