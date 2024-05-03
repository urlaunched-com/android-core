package com.urlaunched.andorid.cdn.domain.usecases

import com.urlaunched.andorid.cdn.domain.repository.DownloadCdnRepository
import com.urlaunched.android.cdn.models.domain.download.DownloadableCdnDomainModel

class DownloadCdnFileUseCase(private val repository: DownloadCdnRepository) {
    suspend operator fun invoke(downloadableCdn: DownloadableCdnDomainModel, path: String) =
        repository.downloadFile(downloadableCdn, path)
}