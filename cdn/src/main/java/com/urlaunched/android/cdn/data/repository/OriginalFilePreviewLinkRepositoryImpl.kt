package com.urlaunched.android.cdn.data.repository

import com.urlaunched.android.cdn.data.remote.source.CdnDataSource
import com.urlaunched.android.cdn.domain.repository.OriginalFilePreviewLinkRepository
import com.urlaunched.android.cdnmodels.domain.links.MediaLink

class OriginalFilePreviewLinkRepositoryImpl(private val cdnDataSource: CdnDataSource) : OriginalFilePreviewLinkRepository {
    override suspend fun downloadFile(mediaLink: MediaLink, path: String) = cdnDataSource.downloadFile(mediaLink, path)
    override suspend fun getPrivateFileLink(mediaLink: MediaLink.Private) =
        cdnDataSource.getPrivateFileLink(mediaLink = mediaLink)
}