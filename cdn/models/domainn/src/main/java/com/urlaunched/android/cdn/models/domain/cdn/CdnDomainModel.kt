package com.urlaunched.android.cdn.models.domain.cdn

data class CdnDomainModel(
    val id: Int,
    val cdnRawLink: String,
    val sizeKb: Int?,
    val mediaType: String?
) {
    val bucket = when {
        cdnRawLink.contains(R2_HOST) ->
            cdnRawLink
                .substringAfter("://")
                .substringAfter('/')
                .substringBefore('.')

        else -> {
            cdnRawLink
                .substringAfter("://")
                .substringBefore('.')
        }
    }

    val objectKey = when {
        cdnRawLink.contains(R2_HOST) ->
            cdnRawLink
                .substringAfter("://")
                .substringAfter('/')
                .substringAfter('/')

        else -> {
            cdnRawLink
                .substringAfter("://")
                .substringAfter('/')
        }
    }

    companion object {
        private const val R2_HOST = "r2.cloudflarestorage.com"
    }
}