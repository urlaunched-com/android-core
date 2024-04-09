package com.urlaunched.android.tempattachment.domain.usecases

import com.urlaunched.android.common.files.MediaType
import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.tempattachment.domain.repository.TempAttachmentsRepository
import com.urlaunched.android.tempattachment.models.domain.TempAttachmentsDomainModel
import java.io.File

class UploadFileAndGetPublicUrlUseCase(private val tempAttachmentsRepository: TempAttachmentsRepository) {
    suspend operator fun invoke(mediaType: MediaType, file: File): Response<String> {
        var tempAttachment: TempAttachmentsDomainModel? = null
        var result: Response<String> = Response.Error(ErrorData(null, null))

        handleRequest(
            request = { tempAttachmentsRepository.getPresignedAndPublicUrl(fileName = file.name) },
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
                    tempAttachmentsRepository.uploadFileToPresignedUrl(
                        mediaType = mediaType,
                        file = file,
                        presignedUrl = tempAtt.presignedUrl
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