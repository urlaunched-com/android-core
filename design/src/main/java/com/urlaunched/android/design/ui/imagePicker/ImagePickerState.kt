package com.urlaunched.android.design.ui.imagepicker

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.urlaunched.android.common.compression.CompressImageUtil
import com.urlaunched.android.common.files.FilePickerHelper
import com.urlaunched.android.design.ui.imagepicker.model.CompressionConfig
import com.urlaunched.android.design.ui.imagepicker.model.ImagePickerError
import com.urlaunched.android.design.ui.imagepicker.model.ImagesCount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ImagePickerState(
    val compressionConfig: CompressionConfig = CompressionConfig(),
    val coroutineScope: CoroutineScope,
    val deleteTempFilesOnDispose: Boolean,
    val onError: suspend (error: ImagePickerError) -> Unit,
    val onImagesPicked: (images: List<File>) -> Unit
) {
    var maxImagesCount by mutableStateOf(ImagesCount.SINGLE_IMAGE)
        private set

    var isSourceSelectorDialogShown by mutableStateOf(false)
        private set

    var isFileCopyingProceed by mutableStateOf(false)
        private set

    var isImageCompressionProceed by mutableStateOf(false)
        private set

    fun processPickedUris(uris: List<Uri>, context: Context) {
        coroutineScope.launch(Dispatchers.IO) {
            isFileCopyingProceed = true

            val files = uris.mapNotNull { uri ->
                FilePickerHelper.createFileFromUri(context, uri)
            }

            isFileCopyingProceed = false

            processPickedFiles(files = files, context = context)
        }
    }

    fun processPickedFiles(files: List<File>, context: Context) {
        coroutineScope.launch(Dispatchers.IO) {
            isImageCompressionProceed = true
            val validFiles = mutableListOf<File>()

            files.forEach { file ->
                if (file.length() > compressionConfig.maxSizeBytes) {
                    onError(ImagePickerError.PHOTO_TOO_LARGE)
                } else {
                    try {
                        compressImage(file, context)?.let { compressedFile ->
                            validFiles.add(compressedFile)
                        } ?: onError(ImagePickerError.COMPRESSION_FAILED)
                    } catch (e: Exception) {
                        onError(ImagePickerError.COMPRESSION_FAILED)
                        isImageCompressionProceed = false
                        return@launch
                    }
                }
            }

            onImagesPicked(validFiles)
            isImageCompressionProceed = false
        }
    }

    private fun compressImage(image: File, context: Context): File? {
        return CompressImageUtil.compressImage(
            input = image,
            context = context,
            targetSize = compressionConfig.targetSizeBytes,
            minWidth = compressionConfig.minWidth,
            minHeight = compressionConfig.minHeight
        )
    }

    fun showSourceSelectorDialog(
        maxImagesCount: ImagesCount = ImagesCount.SINGLE_IMAGE
    ) {
        this.maxImagesCount = maxImagesCount
        isSourceSelectorDialogShown = true
    }

    fun hideSourceSelectorDialog() {
        isSourceSelectorDialogShown = false
    }
}

@Composable
fun rememberImagePickerState(
    photoTooLargeErrorMessage: String,
    compressionErrorMessage: String,
    compressionConfig: CompressionConfig = CompressionConfig(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    deleteTempFilesOnDispose: Boolean = true,
    showErrorSnackbar: suspend (message: String) -> Unit,
    onImagesPicked: (images: List<File>) -> Unit
) = rememberImagePickerState(
    compressionConfig = compressionConfig,
    coroutineScope = coroutineScope,
    deleteTempFilesOnDispose = deleteTempFilesOnDispose,
    onImagesPicked = onImagesPicked,
    onError = { error ->
        when (error) {
            ImagePickerError.PHOTO_TOO_LARGE -> showErrorSnackbar(photoTooLargeErrorMessage)
            ImagePickerError.COMPRESSION_FAILED -> showErrorSnackbar(compressionErrorMessage)
        }
    }
)

@Composable
internal fun rememberImagePickerState(
    compressionConfig: CompressionConfig = CompressionConfig(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    deleteTempFilesOnDispose: Boolean = true,
    onError: suspend (error: ImagePickerError) -> Unit,
    onImagesPicked: (images: List<File>) -> Unit
) = remember {
    ImagePickerState(
        compressionConfig = compressionConfig,
        coroutineScope = coroutineScope,
        onError = onError,
        deleteTempFilesOnDispose = deleteTempFilesOnDispose,
        onImagesPicked = onImagesPicked
    )
}