package com.urlaunched.android.design.ui.camera.util

import android.content.Context
import com.urlaunched.android.design.ui.camera.model.AppMediaType
import java.io.File

class VideoRecordingTempFileProvider (private val context: Context) {
    operator fun invoke(): File = File(context.cacheDir, CacheFoldersConstants.VIDEO_RECORDINGS).apply {
        mkdir()
    }.let { dir ->
        File.createTempFile(TEMP_FILE_PREFIX, AppMediaType.VIDEO.extensions.first(), dir)
    }

    companion object {
        private const val TEMP_FILE_PREFIX = "video"
    }
}