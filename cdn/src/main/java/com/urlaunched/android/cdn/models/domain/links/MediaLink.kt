package com.urlaunched.android.cdn.models.domain.links

import kotlinx.serialization.Serializable

@Serializable
sealed class MediaLink {
    abstract val id: Int
    abstract val link: String
    abstract val sizeKb: Int

    @Serializable
    data class Public(
        override val id: Int,
        override val link: String,
        override val sizeKb: Int
    ) : MediaLink()

    @Serializable
    data class Private(
        override val id: Int,
        override val link: String,
        override val sizeKb: Int
    ) : MediaLink()
}