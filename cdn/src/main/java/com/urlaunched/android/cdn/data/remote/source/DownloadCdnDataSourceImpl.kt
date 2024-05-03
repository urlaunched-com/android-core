package com.urlaunched.android.cdn.data.remote.source

import com.urlaunched.android.cdn.data.remote.api.CdnMediaApi
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.common.response.map
import com.urlaunched.android.models.domain.download.DownloadState
import com.urlaunched.android.models.domain.download.DownloadableCdnDomainModel
import com.urlaunched.android.network.utils.executeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class DownloadCdnDataSourceImpl(
    private val cdnMediaApi: CdnMediaApi,
    private val okHttpClient: OkHttpClient
) : DownloadCdnDataSource {
    override suspend fun downloadFile(downloadableCdn: DownloadableCdnDomainModel, path: String): Flow<DownloadState> = try {
        when (downloadableCdn) {
            is DownloadableCdnDomainModel.Public -> {
                downloadFileWithStreaming(downloadableCdn.link, path)
            }

            is DownloadableCdnDomainModel.Private -> {
                when (
                    val response = executeRequest { cdnMediaApi.generateDownloadLink(downloadableCdn.id) }
                ) {
                    is Response.Success -> {
                        downloadFileWithStreaming(response.data.downloadLink, path)
                    }

                    is Response.Error -> {
                        flowOf(DownloadState.Failed(RuntimeException(response.error.message)))
                    }
                }
            }
        }
    } catch (e: Exception) {
        flowOf(DownloadState.Failed(e))
    }

    override suspend fun getPrivateFileLink(downloadableCdn: DownloadableCdnDomainModel.Private): Response<String> = executeRequest {
        cdnMediaApi.generateDownloadLink(downloadableCdn.id)
    }.map {
        it.downloadLink
    }

    private fun downloadFileWithStreaming(link: String, path: String): Flow<DownloadState> = flow {
        try {
            val request = Request.Builder()
                .url(link)
                .build()

            emit(DownloadState.Downloading(0))

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }
                val destinationFile = File("$path.${link.split(".").lastOrNull()?.substringBefore("?")}")

                response.body?.byteStream()?.use { inputStream ->
                    destinationFile.outputStream().use { outputStream ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var progressBytes = 0L
                        var bytes = inputStream.read(buffer)
                        val kb = 1024

                        while (bytes >= 0) {
                            outputStream.write(buffer, 0, bytes)
                            progressBytes += bytes
                            bytes = inputStream.read(buffer)

                            emit(DownloadState.Downloading(progressBytes / kb))
                        }

                        emit(DownloadState.Finished(progressBytes / kb))
                    }
                }
            }
        } catch (e: Exception) {
            emit(DownloadState.Failed(e))
        }
    }
}