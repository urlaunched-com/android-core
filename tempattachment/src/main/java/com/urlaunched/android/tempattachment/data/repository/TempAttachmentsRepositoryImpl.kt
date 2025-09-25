package com.urlaunched.android.tempattachment.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.urlaunched.android.common.files.MediaType
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.common.response.map
import com.urlaunched.android.common.response.wrapResponseFlatten
import com.urlaunched.android.network.utils.asRequestBody
import com.urlaunched.android.network.utils.executeOkhttpRequest
import com.urlaunched.android.tempattachment.data.remote.datasource.TempAttachmentsRemoteDataSource
import com.urlaunched.android.tempattachment.domain.repository.TempAttachmentsRepository
import com.urlaunched.android.tempattachment.models.remote.toDomainModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class TempAttachmentsRepositoryImpl(
    private val tempAttachmentsRemoteDataSource: TempAttachmentsRemoteDataSource,
    private val okHttpClient: OkHttpClient,
    private val context: Context
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

    override suspend fun uploadFileToPresignedUrl(
        mediaType: MediaType,
        uri: Uri,
        presignedUrl: String
    ): Response<Unit> = wrapResponseFlatten {
        val requestBody = uri.asRequestBody(
            contentResolver = context.contentResolver,
            contentType = mediaType.mimeType.toMediaTypeOrNull()
        )

        val request = Request.Builder()
            .url(presignedUrl)
            .put(requestBody)
            .build()

        executeOkhttpRequest { okHttpClient.newCall(request) }
    }

    override suspend fun uploadBitmapToPresignedUrl(
        mediaType: MediaType,
        bitmap: Bitmap,
        presignedUrl: String,
        compress: Int
    ): Response<Unit> = wrapResponseFlatten {
        val format = Bitmap.CompressFormat.JPEG
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, compress, stream)

        val requestBody = stream
            .toByteArray()
            .toRequestBody(mediaType.mimeType.toMediaTypeOrNull())

        val request = Request.Builder()
            .url(presignedUrl)
            .put(requestBody)
            .build()

        executeOkhttpRequest { okHttpClient.newCall(request) }
    }
}