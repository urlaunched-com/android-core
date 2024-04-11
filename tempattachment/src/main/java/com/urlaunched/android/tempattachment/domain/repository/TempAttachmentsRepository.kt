package com.urlaunched.android.tempattachment.domain.repository

import com.urlaunched.android.common.files.MediaType
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.tempattachment.models.domain.TempAttachmentsDomainModel
import java.io.File

interface TempAttachmentsRepository {
    suspend fun getPresignedAndPublicUrl(fileName: String, isPrivate: Boolean): Response<TempAttachmentsDomainModel>
    suspend fun uploadFileToPresignedUrl(mediaType: MediaType, file: File, presignedUrl: String): Response<Unit>
}