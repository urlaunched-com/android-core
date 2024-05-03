package com.urlaunched.android.cdnmodels.domain.media

import kotlinx.serialization.Serializable

@Serializable
data class MediaDomainModel(
    val id: Int,
    val mediaRawLink: String,
    val sizeKb: Int?,
    val mediaType: String?
)