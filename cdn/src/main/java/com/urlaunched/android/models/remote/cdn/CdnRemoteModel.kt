package com.urlaunched.android.models.remote.cdn

import com.urlaunched.android.models.domain.cdn.CdnDomainModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CdnRemoteModel(
    @SerialName("id")
    val id: Int,
    @SerialName("media_type")
    val mediaType: String?,
    @SerialName("media_url")
    val mediaUrl: String,
    @SerialName("size_kb")
    val sizeKb: Int?
)

fun CdnRemoteModel.toDomainModel() = CdnDomainModel(
    id = id,
    cdnRawLink = mediaUrl,
    sizeKb = sizeKb
)