package com.urlaunched.android.models.remote.download

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateDownloadLinkRemoteModel(
    @SerialName("download_link")
    val downloadLink: String
)