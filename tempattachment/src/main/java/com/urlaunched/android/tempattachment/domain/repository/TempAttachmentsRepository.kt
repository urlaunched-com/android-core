package com.urlaunched.android.tempattachment.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.urlaunched.android.common.files.MediaType
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.tempattachment.models.domain.TempAttachmentsDomainModel
import java.io.File

interface TempAttachmentsRepository {
    suspend fun getPresignedAndPublicUrl(fileName: String, isPrivate: Boolean): Response<TempAttachmentsDomainModel>
    suspend fun uploadFileToPresignedUrl(mediaType: MediaType, file: File, presignedUrl: String): Response<Unit>
    suspend fun uploadFileToPresignedUrl(mediaType: MediaType, uri: Uri, presignedUrl: String): Response<Unit>
    suspend fun uploadBitmapToPresignedUrl(
        mediaType: MediaType,
        bitmap: Bitmap,
        presignedUrl: String,
        compress: Int = 100
    ): Response<Unit>
}