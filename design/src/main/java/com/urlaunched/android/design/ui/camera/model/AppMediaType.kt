package com.urlaunched.android.design.ui.camera.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.urlaunched.android.common.files.MediaType
import com.urlaunched.android.design.ui.camera.util.mimeToMediaType

enum class AppMediaType(
    override val mimeType: String,
    override val extraMimeTypes: List<String> = listOf(),
    val extensions: List<String> = listOf()
) : MediaType {
    IMAGE_PREVIEW(mimeType = "image/*", extensions = listOf(".jpg")),
    VIDEO(mimeType = "video/mp4", extensions = listOf(".mp4"))
}

fun Uri.getMediaTypeForUri(context: Context) = if (ContentResolver.SCHEME_CONTENT == scheme) {
    context.contentResolver.getType(this)?.mimeToMediaType()
} else {
    MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(toString()))
        ?.mimeToMediaType()
}