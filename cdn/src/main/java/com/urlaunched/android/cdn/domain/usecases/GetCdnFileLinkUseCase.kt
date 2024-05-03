package com.urlaunched.android.cdn.domain.usecases

import com.urlaunched.android.cdn.domain.repository.DownloadCdnRepository
import com.urlaunched.android.models.domain.download.DownloadableCdnDomainModel

class GetCdnFileLinkUseCase(private val repository: DownloadCdnRepository) {
    suspend operator fun invoke(downloadableCdn: DownloadableCdnDomainModel.Private) = repository.getPrivateFileLink(downloadableCdn)
}