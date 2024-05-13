package com.urlaunched.android.cdn.models.presentation.image

import com.urlaunched.android.cdn.models.domain.cdn.CdnDomainModel
import com.urlaunched.android.cdn.models.domain.download.DownloadableCdnDomainModel
import com.urlaunched.android.cdn.models.presentation.CdnConfig
import com.urlaunched.android.cdn.models.presentation.image.transform.Edits
import com.urlaunched.android.cdn.models.presentation.image.transform.TransformData
import com.urlaunched.android.cdn.models.presentation.utils.SensitiveApi
import com.urlaunched.android.cdn.models.presentation.utils.toBase64
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class CdnImagePresentationModel {
    @Serializable
    data class Public(
        val id: Int,
        val sizeKb: Int?,
        val mediaType: String?,
        private val cdnRawLink: String,
        private val cdnConfig: CdnConfig
    ) : CdnImagePresentationModel() {
        private val bucket = cdnRawLink
            .substringAfter("://")
            .substringBefore('.')

        private val objectKey = cdnRawLink
            .substringAfter("://")
            .substringAfter('/')

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

        fun toDomainModel() =
            CdnDomainModel(
                id = id,
                cdnRawLink = cdnRawLink,
                mediaType = mediaType,
                sizeKb = sizeKb
            )
    }

    @Serializable
    data class Private(
        val id: Int,
        val sizeKb: Int?,
        val mediaType: String?,
        val link: String,
        val cdnRawLink: String
    ) : CdnImagePresentationModel() {
        fun toDomainModel() =
            CdnDomainModel(
                id = id,
                cdnRawLink = cdnRawLink,
                mediaType = mediaType,
                sizeKb = sizeKb
            )
    }
}

fun CdnDomainModel.toCdnPublicImagePresentationModel(cdnConfig: CdnConfig): CdnImagePresentationModel.Public =
    CdnImagePresentationModel.Public(
        id = id,
        cdnRawLink = cdnRawLink,
        sizeKb = sizeKb,
        cdnConfig = cdnConfig,
        mediaType = mediaType
    )

fun CdnDomainModel.toCdnPrivateImagePresentationModel(cdnConfig: CdnConfig): CdnImagePresentationModel.Private =
    CdnImagePresentationModel.Private(
        id = id,
        sizeKb = sizeKb,
        cdnRawLink = cdnRawLink,
        mediaType = mediaType,
        link = "${cdnConfig.privateMediaEndpoint}/$id"
    )

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