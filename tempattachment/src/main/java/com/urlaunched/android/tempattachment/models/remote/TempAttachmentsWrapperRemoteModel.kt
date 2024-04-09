package com.urlaunched.android.tempattachment.models.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TempAttachmentsWrapperRemoteModel(
    @SerialName("attachment")
    val attachment: TempAttachmentsRemoteModel
)