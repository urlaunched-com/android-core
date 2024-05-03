package com.urlaunched.android.cdn.domain.repository

import com.urlaunched.android.common.response.Response
import com.urlaunched.android.models.domain.download.DownloadState
import com.urlaunched.android.models.domain.download.DownloadableCdnDomainModel
import kotlinx.coroutines.flow.Flow

interface DownloadCdnRepository {
    suspend fun getPrivateFileLink(downloadableCdn: DownloadableCdnDomainModel.Private): Response<String>
    suspend fun downloadFile(downloadableCdn: DownloadableCdnDomainModel, path: String): Flow<DownloadState>
}