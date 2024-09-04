package com.urlaunched.android.cdn.models.domain.download

import java.io.File

sealed class DownloadState {
    data class Downloading(val progress: Long) : DownloadState()
    data class Finished(val progress: Long, val file: File) : DownloadState()
    data class Failed(val error: Throwable? = null) : DownloadState()
}