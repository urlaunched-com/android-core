package com.urlaunched.android.cdn.data.remote.api

import com.urlaunched.android.cdn.models.remote.download.GenerateDownloadLinkRemoteModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CdnMediaApi {
    @GET("/api/v1/signed_downloads/{media_id}")
    suspend fun generateDownloadLink(@Path("media_id") mediaId: Int): Response<GenerateDownloadLinkRemoteModel>
}