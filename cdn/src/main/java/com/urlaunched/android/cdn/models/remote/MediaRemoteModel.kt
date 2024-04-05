package com.urlaunched.android.cdn.models.remote

import com.urlaunched.android.cdn.models.domain.media.MediaDomainModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaRemoteModel(
    @SerialName("id")
    val id: Int,
    @SerialName("media_type")
    val mediaType: String,
    @SerialName("media_url")
    val mediaUrl: String,
    @SerialName("size_kb")
    val sizeKb: Int
)

fun MediaRemoteModel.toCDNMedia() = MediaDomainModel(
    id = id,
    mediaRawLink = mediaUrl,
    sizeKb = sizeKb
)