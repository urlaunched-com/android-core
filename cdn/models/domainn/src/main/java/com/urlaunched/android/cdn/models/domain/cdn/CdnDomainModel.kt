package com.urlaunched.android.cdn.models.domain.cdn

data class CdnDomainModel(
    val id: Int,
    val cdnRawLink: String,
    val sizeKb: Int?,
    val mediaType: String?,
    val link: String
) {
    val bucket = cdnRawLink
        .substringAfter("://")
        .substringBefore('.')

    val objectKey = cdnRawLink
        .substringAfter("://")
        .substringAfter('/')
}