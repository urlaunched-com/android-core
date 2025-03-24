package com.urlaunched.android.common.compression

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

object CompressImageUtil {
    fun compressImage(input: File, context: Context, outDir: File = context.cacheDir): File? = compressImage(
        input.toUri(), context, outDir
    )

    fun compressImage(input: Uri, context: Context, outDir: File = context.cacheDir): File? {
        val compressedFile = File(outDir, "compressed_${Instant.now().toEpochMilli()}.jpg")
        val originalBitmap = context.contentResolver.openInputStream(input).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }

        var targetWidth = originalBitmap.width
        var targetHeight = originalBitmap.height
        val targetSize = 5 * 1000 * 1000

        var resizedBitmap = originalBitmap.scale(targetWidth, targetHeight)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var fileSize: Long

        do {
            byteArrayOutputStream.reset()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

            val byteArray = byteArrayOutputStream.toByteArray()
            fileSize = byteArray.size.toLong()

            if (fileSize > targetSize) {
                targetWidth = (targetWidth * 0.9).toInt()
                targetHeight = (targetHeight * 0.9).toInt()

                if (targetWidth * targetHeight < 1920 * 1080) {
                    break
                }

                resizedBitmap = originalBitmap.scale(targetWidth, targetHeight)
            }
        } while (fileSize > targetSize)

        if (fileSize > targetSize) {
            resizedBitmap.recycle()
            return null
        }

        FileOutputStream(compressedFile).use { fos ->
            fos.write(byteArrayOutputStream.toByteArray())
        }

        resizedBitmap.recycle()

        return compressedFile
    }
}