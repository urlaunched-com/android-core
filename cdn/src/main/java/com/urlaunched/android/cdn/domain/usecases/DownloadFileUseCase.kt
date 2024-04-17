package com.urlaunched.android.cdn.domain.usecases

import com.urlaunched.android.cdn.domain.repository.OriginalFilePreviewLinkRepository
import com.urlaunched.android.cdnmodels.domain.links.MediaLink

class DownloadFileUseCase(private val repository: OriginalFilePreviewLinkRepository) {
    suspend operator fun invoke(mediaLink: MediaLink, path: String) = repository.downloadFile(mediaLink, path)
}