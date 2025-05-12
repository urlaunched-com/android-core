package com.urlaunched.android.cdn.models.presentation.image

import android.net.Uri
import com.urlaunched.android.cdn.models.domain.cdn.CdnDomainModel
import com.urlaunched.android.cdn.models.presentation.CdnConfig
import com.urlaunched.android.cdn.models.presentation.utils.SensitiveApi
import kotlinx.serialization.Serializable

@Serializable
data class CdnResizableImagePresentationModel(
    val id: Int,
    val sizeKb: Int?,
    val mediaType: String?,
    private val cdnRawLink: String,
    private val cdnConfig: CdnConfig
) {
    private val objectKey = cdnRawLink
        .substringAfter("://")
        .substringAfter('/')

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
    }
}

fun CdnDomainModel.toCdnResizableImagePresentationModel(cdnConfig: CdnConfig): CdnResizableImagePresentationModel =
    CdnResizableImagePresentationModel(
        id = id,
        cdnRawLink = cdnRawLink,
        sizeKb = sizeKb,
        mediaType = mediaType,
        cdnConfig = cdnConfig
    )