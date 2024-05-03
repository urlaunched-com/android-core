package com.urlaunched.android.cdn.data.remote.source

import com.urlaunched.android.common.response.Response
import com.urlaunched.android.models.domain.download.DownloadState
import com.urlaunched.android.models.domain.download.DownloadableCdnDomainModel
import kotlinx.coroutines.flow.Flow

interface DownloadCdnDataSource {
    suspend fun downloadFile(downloadableCdn: DownloadableCdnDomainModel, path: String): Flow<DownloadState>
    suspend fun getPrivateFileLink(downloadableCdn: DownloadableCdnDomainModel.Private): Response<String>
}