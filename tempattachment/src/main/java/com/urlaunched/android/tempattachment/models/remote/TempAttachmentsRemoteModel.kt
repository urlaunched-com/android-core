package com.urlaunched.android.tempattachment.models.remote

import com.urlaunched.android.tempattachment.models.domain.TempAttachmentsDomainModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TempAttachmentsRemoteModel(
    @SerialName("public_url")
    val publicUrl: String,
    @SerialName("presigned_url")
    val presignedUrl: String
)

fun TempAttachmentsRemoteModel.toDomainModel() = TempAttachmentsDomainModel(
    publicUrl = publicUrl,
    presignedUrl = presignedUrl
)