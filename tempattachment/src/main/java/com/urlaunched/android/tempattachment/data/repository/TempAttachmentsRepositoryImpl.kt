package com.urlaunched.android.tempattachment.data.repository

import com.urlaunched.android.common.files.MediaType
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.common.response.map
import com.urlaunched.android.network.utils.executeOkhttpRequest
import com.urlaunched.android.tempattachment.data.remote.datasource.TempAttachmentsRemoteDataSource
import com.urlaunched.android.tempattachment.domain.repository.TempAttachmentsRepository
import com.urlaunched.android.tempattachment.models.remote.toDomainModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class TempAttachmentsRepositoryImpl(
    private val tempAttachmentsRemoteDataSource: TempAttachmentsRemoteDataSource,
    private val okHttpClient: OkHttpClient
) : TempAttachmentsRepository {
    override suspend fun getPresignedAndPublicUrl(fileName: String, isPrivate: Boolean) =
        tempAttachmentsRemoteDataSource.sendFile(fileName = fileName, isPrivate = isPrivate).map {
            it.attachment.toDomainModel()
        }

    override suspend fun uploadFileToPresignedUrl(
        mediaType: MediaType,
        file: File,
        presignedUrl: String
    ): Response<Unit> {
        val requestBody = file.asRequestBody(mediaType.mimeType.toMediaTypeOrNull())

        val request = Request.Builder()
            .url(presignedUrl)
            .put(requestBody)
            .build()

        return executeOkhttpRequest { okHttpClient.newCall(request) }
    }
}