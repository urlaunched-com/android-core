package com.urlaunched.android.tempattachment.domain.usecases

import com.urlaunched.android.common.files.MediaType
import com.urlaunched.android.tempattachment.domain.repository.TempAttachmentsRepository
import java.io.File

class UploadToPresignedUrlUseCase(private val tempAttachmentsRepository: TempAttachmentsRepository) {
    suspend operator fun invoke(mediaType: MediaType, file: File, presignedUrl: String) =
        tempAttachmentsRepository.uploadFileToPresignedUrl(mediaType, file, presignedUrl)
}