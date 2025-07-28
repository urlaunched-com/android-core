package com.urlaunched.android.common.compression

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.graphics.scale
import androidx.core.net.toUri
import okio.use
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object CompressImageUtil {
    private const val DEFAULT_TARGET_SIZE_BYTES: Long = 5 * 1000 * 1000
    private const val DEFAULT_MIN_HEIGHT = 1080
    private const val DEFAULT_MIN_WIDTH = 1920

    fun compressImage(
        input: File,
        context: Context,
        outDir: File = context.cacheDir,
        targetSize: Long = DEFAULT_TARGET_SIZE_BYTES,
        minWidth: Int = DEFAULT_MIN_WIDTH,
        minHeight: Int = DEFAULT_MIN_HEIGHT
    ): File? = compressImage(
        input = input.toUri(),
        context = context,
        outDir = outDir,
        targetSize = targetSize,
        minWidth = minWidth,
        minHeight = minHeight
    )

    fun compressImage(
        input: Uri,
        context: Context,
        outDir: File = context.cacheDir,
        targetSize: Long = DEFAULT_TARGET_SIZE_BYTES,
        minWidth: Int = DEFAULT_MIN_WIDTH,
        minHeight: Int = DEFAULT_MIN_HEIGHT
    ): File? {
        if (!outDir.exists()) {
            outDir.mkdirs()
        }

        val compressedFile = File(outDir, "compressed_${UUID.randomUUID()}.jpg")
        val originalBitmap = context.contentResolver.openInputStream(input).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }

        val orientation = context.contentResolver.openInputStream(input)?.use { inputStream ->
            val exif = ExifInterface(inputStream)
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL

        val rotatedBitmap = rotateBitmapIfNeeded(originalBitmap, orientation)

        var targetWidth = rotatedBitmap.width
        var targetHeight = rotatedBitmap.height

        var resizedBitmap = rotatedBitmap.scale(targetWidth, targetHeight)

        return ByteArrayOutputStream().use { byteArrayOutputStream ->
            var fileSize: Long

            do {
                byteArrayOutputStream.reset()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

                val byteArray = byteArrayOutputStream.toByteArray()
                fileSize = byteArray.size.toLong()

                if (fileSize > targetSize) {
                    targetWidth = (targetWidth * 0.9).toInt()
                    targetHeight = (targetHeight * 0.9).toInt()

                    if (targetWidth * targetHeight < minWidth * minHeight) {
                        break
                    }

                    resizedBitmap = rotatedBitmap.scale(targetWidth, targetHeight)
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

            compressedFile
        }
    }

    private fun rotateBitmapIfNeeded(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            else -> return bitmap // orientation ok or not found
        }

        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotatedBitmap != bitmap) {
            bitmap.recycle()
        }
        return rotatedBitmap
    }
}