package com.urlaunched.android.common.files

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class PickMultipleFilesContract : ActivityResultContract<MediaType, List<Uri>>() {
    override fun createIntent(context: Context, input: MediaType): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = input.mimeType
            if (input.extraMimeTypes.isNotEmpty()) {
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    input.extraMimeTypes.toTypedArray()
                )
            }
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        return intent.takeIf {
            resultCode == Activity.RESULT_OK
        }?.getClipDataUris() ?: emptyList()
    }

    internal companion object {
        private fun Intent.getClipDataUris(): List<Uri> {
            // Use a LinkedHashSet to maintain any ordering that may be
            // present in the ClipData
            val resultSet = LinkedHashSet<Uri>()
            data?.let { data ->
                resultSet.add(data)
            }
            val clipData = clipData
            if (clipData == null && resultSet.isEmpty()) {
                return emptyList()
            } else if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        resultSet.add(uri)
                    }
                }
            }
            return resultSet.toList()
        }
    }
}