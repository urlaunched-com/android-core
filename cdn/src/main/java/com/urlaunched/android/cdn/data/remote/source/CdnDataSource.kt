package com.urlaunched.android.cdn.data.remote.source

import com.urlaunched.android.cdn.models.domain.downloadstate.DownloadState
import com.urlaunched.android.cdn.models.domain.links.MediaLink
import com.urlaunched.android.common.response.Response
import kotlinx.coroutines.flow.Flow

interface CdnDataSource {
    suspend fun downloadFile(mediaLink: MediaLink, path: String): Flow<DownloadState>
    suspend fun getPrivateFileLink(mediaLink: MediaLink.Private): Response<String>
}