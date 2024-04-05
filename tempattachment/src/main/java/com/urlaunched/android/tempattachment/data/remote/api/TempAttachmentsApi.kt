package com.urlaunched.android.tempattachment.data.remote.api

import com.urlaunched.android.tempattachment.models.remote.TempAttachmentsParamsRemoteModel
import com.urlaunched.android.tempattachment.models.remote.TempAttachmentsWrapperRemoteModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TempAttachmentsApi {
    @POST("api/v1/temp_attachments")
    suspend fun sendFile(@Body params: TempAttachmentsParamsRemoteModel): Response<TempAttachmentsWrapperRemoteModel>
}