package com.urlaunched.android.common.files

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object FilePickerHelper {
    private const val THUMBNAIL_FILE_PREFIX = "thumbnail"
    private const val THUMBNAIL_FILE_SUFFIX = ".jpg"

    fun getVideoThumbnail(context: Context, file: File): File? {
        val retriever = MediaMetadataRetriever()
        var thumbnailFile: File? = null

        try {
            retriever.setDataSource(file.absolutePath)
            val frame = retriever.getFrameAtTime(0)

            val cacheDir = context.cacheDir
            thumbnailFile =
                File.createTempFile(THUMBNAIL_FILE_PREFIX, THUMBNAIL_FILE_SUFFIX, cacheDir)

            val outputStream = FileOutputStream(thumbnailFile)
            frame?.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            thumbnailFile?.delete()
        } finally {
            retriever.release()
        }

        return thumbnailFile
    }

    fun getMediaDuration(file: File): Duration? {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(file.absolutePath)

            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationInMillis = durationStr?.toLongOrNull()

            if (durationInMillis != null) {
                return durationInMillis.milliseconds
            }
        } finally {
            retriever.release()
        }

        return null
    }

    fun createFileFromUri(context: Context, uri: Uri): File? {
        val fileName = getFileName(context, uri)
        val fileExtension = '.' + fileName.substringAfter('.')
        val cacheFile = FileHelper.createTempFile(context, fileExtension)

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(cacheFile).use { outputStream ->
                    val buffer = ByteArray(4 * 1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                }
            }
            return cacheFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndexOrThrow("_display_name")
                if (displayNameIndex != -1) {
                    return cursor.getString(displayNameIndex)
                }
            }
        }
        return uri.lastPathSegment ?: "unknown_file"
    }
}