package com.urlaunched.andorid.cdn.domain.repository

import com.urlaunched.android.cdn.models.domain.download.DownloadState
import com.urlaunched.android.cdn.models.domain.download.DownloadableCdnDomainModel
import com.urlaunched.android.common.response.Response
import kotlinx.coroutines.flow.Flow

interface DownloadCdnRepository {
    suspend fun getPrivateFileLink(downloadableCdn: DownloadableCdnDomainModel.Private): Response<String>
    suspend fun downloadFile(downloadableCdn: DownloadableCdnDomainModel, path: String): Flow<DownloadState>
}