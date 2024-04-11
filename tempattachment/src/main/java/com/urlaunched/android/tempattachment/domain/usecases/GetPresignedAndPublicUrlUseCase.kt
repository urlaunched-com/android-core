package com.urlaunched.android.tempattachment.domain.usecases

import com.urlaunched.android.tempattachment.domain.repository.TempAttachmentsRepository

class GetPresignedAndPublicUrlUseCase(private val tempAttachmentsRepository: TempAttachmentsRepository) {
    suspend operator fun invoke(fileName: String, isPrivate: Boolean = false) =
        tempAttachmentsRepository.getPresignedAndPublicUrl(
            fileName = fileName,
            isPrivate = isPrivate
        )
}