package com.urlaunched.android.tempattachment.domain.usecases

import android.graphics.Bitmap
import com.urlaunched.android.common.files.MediaType
import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.tempattachment.domain.repository.TempAttachmentsRepository
import com.urlaunched.android.tempattachment.models.domain.TempAttachmentsDomainModel

class UploadBitmapAndGetPublicUrlUseCase(private val tempAttachmentsRepository: TempAttachmentsRepository) {
    suspend operator fun invoke(
        mediaType: MediaType,
        bitmap: Bitmap,
        fileName: String,
        isPrivate: Boolean = false,
        compress: Int = 100
    ): Response<String> {
        var tempAttachment: TempAttachmentsDomainModel? = null
        var result: Response<String> = Response.Error(ErrorData(null, null))

        handleRequest(
            request = {
                tempAttachmentsRepository.getPresignedAndPublicUrl(
                    fileName = fileName,
                    isPrivate = isPrivate
                )
            },
            success = { attachment ->
                tempAttachment = attachment
            },
            error = { error ->
                result = Response.Error(error)
            }
        )

        tempAttachment?.let { tempAtt ->
            handleRequest(
                request = {
                    tempAttachmentsRepository.uploadBitmapToPresignedUrl(
                        mediaType = mediaType,
                        bitmap = bitmap,
                        presignedUrl = tempAtt.presignedUrl,
                        compress = compress
                    )
                },
                success = {
                    result = Response.Success(tempAtt.publicUrl)
                },
                error = { error ->
                    result = Response.Error(error)
                }
            )
        }

        return result
    }
}

private suspend fun <T : Any> handleRequest(
    request: suspend () -> Response<T>,
    success: suspend (data: T) -> Unit,
    error: suspend (error: ErrorData) -> Unit
) {
    when (val response = request()) {
        is Response.Success -> {
            success(response.data)
        }

        is Response.Error -> {
            error(response.error)
        }
    }
}