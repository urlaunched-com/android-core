package com.urlaunched.android.models.domain.cdn

import kotlinx.serialization.Serializable

@Serializable
data class CdnDomainModel(
    val id: Int,
    val cdnRawLink: String,
    val sizeKb: Int?
) {
    val bucket = cdnRawLink
        .substringAfter("://")
        .substringBefore('.')

    val objectKey = cdnRawLink
        .substringAfter("://")
        .substringAfter('/')
}