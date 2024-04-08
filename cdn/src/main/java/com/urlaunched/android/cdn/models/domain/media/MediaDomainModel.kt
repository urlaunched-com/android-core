package com.urlaunched.android.cdn.models.domain.media

import kotlinx.serialization.Serializable

@Serializable
data class MediaDomainModel(
    val id: Int,
    val mediaRawLink: String,
    val sizeKb: Int
)