package com.urlaunched.android.cdn.models.presentation.image

import android.net.Uri
import com.urlaunched.android.cdn.models.domain.cdn.CdnDomainModel
import com.urlaunched.android.cdn.models.presentation.CdnConfig
import com.urlaunched.android.cdn.models.presentation.utils.SensitiveApi
import kotlinx.serialization.Serializable

@Serializable
data class CdnImage2PresentationModel(
    val id: Int,
    val sizeKb: Int?,
    val mediaType: String?,
    private val cdnRawLink: String,
    private val cdnConfig: CdnConfig
) {
    private val objectKey = when {
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

    @SensitiveApi
    fun originalLink(): String = "${cdnConfig.publicMediaCdn}/$objectKey"

    fun resizedLink(widthPx: Int, heightPx: Int): String = Uri.parse(cdnConfig.publicImageCdn)
        .buildUpon()
        .appendEncodedPath(objectKey)
        .appendQueryParameter(WIDTH_QUERY, widthPx.toString())
        .appendQueryParameter(HEIGHT_QUERY, heightPx.toString())
        .build()
        .toString()

    companion object {
        private const val WIDTH_QUERY = "width"
        private const val HEIGHT_QUERY = "height"
        private const val R2_HOST = "r2.cloudflarestorage.com"
    }
}

fun CdnDomainModel.toCdnImage2PresentationModel(cdnConfig: CdnConfig): CdnImage2PresentationModel =
    CdnImage2PresentationModel(
        id = id,
        cdnRawLink = cdnRawLink,
        sizeKb = sizeKb,
        mediaType = mediaType,
        cdnConfig = cdnConfig
    )