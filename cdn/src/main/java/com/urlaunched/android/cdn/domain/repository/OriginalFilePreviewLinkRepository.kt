package com.urlaunched.android.cdn.domain.repository

import com.urlaunched.android.cdn.models.domain.downloadstate.DownloadState
import com.urlaunched.android.cdn.models.domain.links.MediaLink
import com.urlaunched.android.common.response.Response
import kotlinx.coroutines.flow.Flow

interface OriginalFilePreviewLinkRepository {
    suspend fun getPrivateFileLink(mediaLink: MediaLink.Private): Response<String>
    suspend fun downloadFile(mediaLink: MediaLink, path: String): Flow<DownloadState>
}