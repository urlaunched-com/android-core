package com.urlaunched.android.cdn.models.presentation.cdn

import com.urlaunched.android.cdn.models.domain.cdn.CdnDomainModel
import com.urlaunched.android.cdn.models.domain.download.DownloadableCdnDomainModel
import com.urlaunched.android.cdn.models.presentation.CdnConfig

sealed class CdnPresentationModel {
    abstract val id: Int
    abstract val link: String
    abstract val sizeKb: Int?

    data class Public(
        override val id: Int,
        override val link: String,
        override val sizeKb: Int?
    ) : CdnPresentationModel()

    data class Private(
        override val id: Int,
        override val link: String,
        override val sizeKb: Int?
    ) : CdnPresentationModel()
}

fun CdnDomainModel.toCdnPresentationModel(cdnConfig: CdnConfig): CdnPresentationModel =
    if (cdnRawLink.startsWith(cdnConfig.privateBucket)) {
        CdnPresentationModel.Private(
            id = id,
            link = "${cdnConfig.privateMediaEndpoint}/$id",
            sizeKb = sizeKb
        )
    } else {
        CdnPresentationModel.Public(
            id = id,
            link = "${cdnConfig.publicMediaCdn}/$objectKey",
            sizeKb = sizeKb
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