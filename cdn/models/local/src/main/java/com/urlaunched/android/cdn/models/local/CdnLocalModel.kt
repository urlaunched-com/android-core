package com.urlaunched.android.cdn.models.local

import com.urlaunched.android.cdn.models.domain.cdn.CdnDomainModel
import kotlinx.serialization.Serializable

@Serializable
data class CdnLocalModel(
    val id: Int,
    val cdnRawLink: String,
    val sizeKb: Int?,
    val mediaType: String?
)

fun CdnLocalModel.toDomainModel() =
    CdnDomainModel(
        id = id,
        cdnRawLink = cdnRawLink,
        sizeKb = sizeKb,
        mediaType = mediaType
    )

fun CdnDomainModel.toLocalModel() =
    CdnLocalModel(
        id = id,
        cdnRawLink = cdnRawLink,
        sizeKb = sizeKb,
        mediaType = mediaType
    )