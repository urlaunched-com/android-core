package com.urlaunched.android.cdn.models.presentation.cdn

import com.urlaunched.android.cdn.models.domain.cdn.CdnDomainModel
import com.urlaunched.android.cdn.models.domain.download.DownloadableCdnDomainModel
import com.urlaunched.android.cdn.models.presentation.CdnConfig

sealed class CdnPresentationModel {
    abstract val id: Int
    abstract val link: String
    abstract val sizeKb: Int?
    abstract val mediaType: String?
    abstract val rawLink: String

    data class Public(
        override val id: Int,
        override val link: String,
        override val sizeKb: Int?,
        override val mediaType: String?,
        override val rawLink: String
    ) : CdnPresentationModel()

    data class Private(
        override val id: Int,
        override val link: String,
        override val sizeKb: Int?,
        override val mediaType: String?,
        override val rawLink: String
    ) : CdnPresentationModel()
}

fun CdnDomainModel.toCdnPresentationModel(cdnConfig: CdnConfig): CdnPresentationModel =
    if (cdnRawLink.startsWith(cdnConfig.privateBucket)) {
        CdnPresentationModel.Private(
            id = id,
            link = "${cdnConfig.privateMediaEndpoint}/$id",
            sizeKb = sizeKb,
            mediaType = mediaType,
            rawLink = cdnRawLink
        )
    } else {
        CdnPresentationModel.Public(
            id = id,
            link = "${cdnConfig.publicMediaCdn}/$objectKey",
            sizeKb = sizeKb,
            mediaType = mediaType,
            rawLink = cdnRawLink
        )
    }

fun CdnPresentationModel.Public.toDownloadableCdnModel() = DownloadableCdnDomainModel.Public(
    id = id,
    link = link,
    sizeKb = sizeKb
)

fun CdnPresentationModel.Private.toDownloadableCdnModel() = DownloadableCdnDomainModel.Public(
    id = id,
    link = link,
    sizeKb = sizeKb
)