package com.urlaunched.android.common.imagecropper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.yalantis.ucrop.UCrop
import java.io.File

object ImageCropper {
    fun startCropActivity(
        context: Context,
        cropActivityResultLauncher: ActivityResultLauncher<Intent>,
        imageFile: File,
        aspectRatioX: Float = 1f,
        aspectRatioY: Float = 1f
    ) {
        val sourceUri = Uri.fromFile(imageFile)

        val cacheDir = context.cacheDir
        val file = File.createTempFile(FILE_PREFIX, FILE_SUFFIX, cacheDir)
        val destinationUri = Uri.fromFile(file)

        val options = UCrop.Options().apply {
            setHideBottomControls(true)
            setFreeStyleCropEnabled(false)
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
        }

        val cropIntent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(aspectRatioX, aspectRatioY)
            .getIntent(context)

        cropActivityResultLauncher.launch(cropIntent)
    }

    private const val FILE_PREFIX = "crop"
    private const val FILE_SUFFIX = ".jpg"
}