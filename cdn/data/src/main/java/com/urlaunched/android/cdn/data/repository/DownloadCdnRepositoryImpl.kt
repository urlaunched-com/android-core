package com.urlaunched.android.cdn.data.repository

import com.urlaunched.andorid.cdn.domain.repository.DownloadCdnRepository
import com.urlaunched.android.cdn.data.remote.source.DownloadCdnDataSource
import com.urlaunched.android.cdn.models.domain.download.DownloadableCdnDomainModel

class DownloadCdnRepositoryImpl(private val cdnDataSource: DownloadCdnDataSource) :
    DownloadCdnRepository {
    override suspend fun downloadFile(downloadableCdn: DownloadableCdnDomainModel, path: String) =
        cdnDataSource.downloadFile(downloadableCdn, path)
    override suspend fun getPrivateFileLink(downloadableCdn: DownloadableCdnDomainModel.Private) =
        cdnDataSource.getPrivateFileLink(downloadableCdn = downloadableCdn)
}