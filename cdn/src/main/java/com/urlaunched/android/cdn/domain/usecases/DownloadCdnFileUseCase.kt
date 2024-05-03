package com.urlaunched.android.cdn.domain.usecases

import com.urlaunched.android.cdn.domain.repository.DownloadCdnRepository
import com.urlaunched.android.models.domain.download.DownloadableCdnDomainModel

class DownloadCdnFileUseCase(private val repository: DownloadCdnRepository) {
    suspend operator fun invoke(downloadableCdn: DownloadableCdnDomainModel, path: String) = repository.downloadFile(downloadableCdn, path)
}