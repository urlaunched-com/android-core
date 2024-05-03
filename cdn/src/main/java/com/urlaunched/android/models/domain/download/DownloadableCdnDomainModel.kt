package com.urlaunched.android.models.domain.download

sealed class DownloadableCdnDomainModel {
    abstract val id: Int
    abstract val link: String
    abstract val sizeKb: Int?

    data class Public(
        override val id: Int,
        override val link: String,
        override val sizeKb: Int?
    ) : DownloadableCdnDomainModel()

    data class Private(
        override val id: Int,
        override val link: String,
        override val sizeKb: Int?
    ) : DownloadableCdnDomainModel()
}
