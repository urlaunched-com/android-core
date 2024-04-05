package com.urlaunched.android.tempattachment.data.remote.datasource

import com.urlaunched.android.common.response.Response
import com.urlaunched.android.network.utils.executeRequest
import com.urlaunched.android.tempattachment.data.remote.api.TempAttachmentsApi
import com.urlaunched.android.tempattachment.models.remote.TempAttachmentsParamsRemoteModel
import com.urlaunched.android.tempattachment.models.remote.TempAttachmentsWrapperRemoteModel

class TempAttachmentsRemoteDataSourceImpl(private val api: TempAttachmentsApi) : TempAttachmentsRemoteDataSource {
    override suspend fun sendFile(fileName: String): Response<TempAttachmentsWrapperRemoteModel> = executeRequest {
        api.sendFile(params = TempAttachmentsParamsRemoteModel(fileName))
    }
}