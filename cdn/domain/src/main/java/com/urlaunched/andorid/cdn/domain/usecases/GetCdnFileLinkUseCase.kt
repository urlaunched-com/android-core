package com.urlaunched.andorid.cdn.domain.usecases

import com.urlaunched.andorid.cdn.domain.repository.DownloadCdnRepository
import com.urlaunched.android.cdn.models.domain.download.DownloadableCdnDomainModel

class GetCdnFileLinkUseCase(private val repository: DownloadCdnRepository) {
    suspend operator fun invoke(downloadableCdn: DownloadableCdnDomainModel.Private) =
        repository.getPrivateFileLink(downloadableCdn)
}