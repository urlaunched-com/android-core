package com.urlaunched.android.tempattachment.data.remote.datasource

import com.urlaunched.android.common.response.Response
import com.urlaunched.android.tempattachment.models.remote.TempAttachmentsWrapperRemoteModel

interface TempAttachmentsRemoteDataSource {
    suspend fun sendFile(fileName: String, isPrivate: Boolean): Response<TempAttachmentsWrapperRemoteModel>
}