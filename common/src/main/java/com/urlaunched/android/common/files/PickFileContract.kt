package com.urlaunched.android.common.files

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class PickFileContract : ActivityResultContract<MediaType, Uri?>() {
    override fun createIntent(context: Context, input: MediaType): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = input.mimeType
            if (input.extraMimeTypes.isNotEmpty()) {
                putExtra(Intent.EXTRA_MIME_TYPES, input.extraMimeTypes.toTypedArray())
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            return intent.data
        }
        return null
    }
}