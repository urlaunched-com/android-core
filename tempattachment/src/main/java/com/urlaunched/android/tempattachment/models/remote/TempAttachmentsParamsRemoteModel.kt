package com.urlaunched.android.tempattachment.models.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TempAttachmentsParamsRemoteModel(
    @SerialName("filename")
    val fileName: String,
    @SerialName("acl")
    val access: String?
)