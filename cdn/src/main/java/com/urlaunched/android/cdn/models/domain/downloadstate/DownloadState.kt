package com.urlaunched.android.cdn.models.domain.downloadstate

sealed class DownloadState {
    data class Downloading(val progress: Long) : DownloadState()
    data class Finished(val progress: Long) : DownloadState()
    data class Failed(val error: Throwable? = null) : DownloadState()
}