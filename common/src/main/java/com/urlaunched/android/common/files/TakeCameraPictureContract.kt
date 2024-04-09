package com.urlaunched.android.common.files

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import java.io.File

class TakeCameraPictureContract(private val fileProviderAuthority: String) : ActivityResultContract<Unit, File?>() {
    private var currentPhotoFile: File? = null

    override fun createIntent(context: Context, input: Unit): Intent {
        currentPhotoFile = FileHelper.createTempCameraPickFile(context, IMAGE_EXTENSION)

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            currentPhotoFile?.let { file ->
                putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(
                        context,
                        fileProviderAuthority,
                        file
                    )
                )
            }
        }
    }

    // Intent return nothing. return value of file
    override fun parseResult(resultCode: Int, intent: Intent?): File? = if (resultCode == Activity.RESULT_OK) {
        currentPhotoFile
    } else {
        null
    }

    companion object {
        private const val IMAGE_EXTENSION = ".jpg"
    }
}