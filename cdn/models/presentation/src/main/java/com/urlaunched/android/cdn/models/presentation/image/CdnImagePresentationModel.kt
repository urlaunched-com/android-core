package com.urlaunched.android.cdn.models.presentation.image

import com.urlaunched.android.cdn.models.domain.cdn.CdnDomainModel
import com.urlaunched.android.cdn.models.domain.download.DownloadableCdnDomainModel
import com.urlaunched.android.cdn.models.presentation.CdnConfig
import com.urlaunched.android.cdn.models.presentation.image.transform.Edits
import com.urlaunched.android.cdn.models.presentation.image.transform.TransformData
import com.urlaunched.android.cdn.models.presentation.utils.SensitiveApi
import com.urlaunched.android.cdn.models.presentation.utils.toBase64
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class CdnImagePresentationModel {
    data class Public(
        val id: Int,
        val sizeKb: Int?,
        val mediaType: String?,
        private val cdnRawLink: String,
        private val bucket: String,
        private val objectKey: String,
        private val cdnConfig: CdnConfig
    ) : CdnImagePresentationModel() {
        @SensitiveApi
        fun originalLink(): String = "${cdnConfig.publicMediaCdn}/$objectKey"

        fun resizedLink(edits: Edits): String {
            val transformData = TransformData(
                bucket = bucket,
                objectKey = objectKey,
                edits = edits
            )
            val rawJson = Json.encodeToString(transformData)

            return "${cdnConfig.publicImageCdn}/${rawJson.toBase64()}"
        }
    }

    data class Private(
        val id: Int,
        val sizeKb: Int?,
        val mediaType: String?,
        val link: String
    ) : CdnImagePresentationModel()
}

fun CdnDomainModel.toCdnImagePresentationModel(cdnConfig: CdnConfig): CdnImagePresentationModel =
    if (cdnRawLink.startsWith(cdnConfig.privateBucket)) {
        CdnImagePresentationModel.Private(
            id = id,
            sizeKb = sizeKb,
            link = "${cdnConfig.privateMediaEndpoint}/$id",
            mediaType = mediaType
        )
    } else {
        CdnImagePresentationModel.Public(
            id = id,
            cdnRawLink = cdnRawLink,
            sizeKb = sizeKb,
            bucket = bucket,
            objectKey = objectKey,
            cdnConfig = cdnConfig,
            mediaType = mediaType
        )
    }

@OptIn(SensitiveApi::class)
fun CdnImagePresentationModel.Public.toDownloadableCdnModel(edits: Edits?) = DownloadableCdnDomainModel.Public(
    id = id,
    link = if (edits == null) originalLink() else resizedLink(edits),
    sizeKb = sizeKb
)

fun CdnImagePresentationModel.Private.toDownloadableCdnModel() = DownloadableCdnDomainModel.Public(
    id = id,
    link = link,
    sizeKb = sizeKb
)