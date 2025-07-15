package com.urlaunched.android.design.ui.imagePicker

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.urlaunched.android.common.compression.CompressImageUtil
import com.urlaunched.android.common.files.FileHelper
import com.urlaunched.android.common.files.FilePickerHelper
import com.urlaunched.android.common.files.TakeCameraPictureContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private const val FILE_PROVIDER = ".provider"

@OptIn(ExperimentalPermissionsApi::class)
@Suppress("ktlint:ktlintrules:composable-modifier-missing-rule")
@Composable
fun ImagePicker(
    dialogModifier: Modifier = Modifier,
    dialogConfig: ImagePickerDialogConfig,
    onFilesChanges: ((file: List<File>) -> Unit)?,
    deleteTempFilesWhenOnDispose: Boolean = true,
    maxFiles: Int = ImagePickerConstants.MAX_PHOTOS_AMOUNT,
    needToPickMultipleFiles: Boolean = false,
    showSnackbar: suspend (message: String) -> Unit,
    setIsCompressionProceed: (isCompressionProceed: Boolean) -> Unit = {},
    content: @Composable (onClick: () -> Unit) -> Unit
) {
    if (!LocalInspectionMode.current) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var showSelectorDialog by remember { mutableStateOf(false) }

        val cameraPickerContract =
            remember { TakeCameraPictureContract(fileProviderAuthority = context.packageName + FILE_PROVIDER) }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = cameraPickerContract,
            onResult = { file ->
                if (file != null) {
                    validateFilesAndCompress(
                        files = listOf(file),
                        context = context,
                        coroutineScope = coroutineScope,
                        onFileChanges = onFilesChanges,
                        showSnackbar = showSnackbar,
                        setIsCompressionProceed = setIsCompressionProceed
                    )
                }
            }
        )

        val pickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let {
                    FilePickerHelper.createFileFromUri(context, it)?.let { file ->
                        validateFilesAndCompress(
                            files = listOf(file),
                            context = context,
                            coroutineScope = coroutineScope,
                            onFileChanges = onFilesChanges,
                            showSnackbar = showSnackbar,
                            setIsCompressionProceed = setIsCompressionProceed
                        )
                    }
                }
            }
        )

        val multiplePhotoPickerLauncher = if (maxFiles > 1) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxFiles),
                onResult = { uris ->
                    val files = mutableListOf<File>()
                    uris.forEach {
                        FilePickerHelper.createFileFromUri(context, it)?.let { file ->
                            files.add(file)
                        }
                    }
                    validateFilesAndCompress(
                        files = files,
                        context = context,
                        coroutineScope = coroutineScope,
                        onFileChanges = onFilesChanges,
                        showSnackbar = showSnackbar,
                        setIsCompressionProceed = setIsCompressionProceed
                    )
                }
            )
        } else {
            null
        }

        val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

        val cameraRequestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                launchCameraPicker(cameraLauncher)
            }
        }

        if (deleteTempFilesWhenOnDispose) {
            DisposableEffect(Unit) {
                onDispose {
                    FileHelper.deleteTempFilesFromCache(context)
                }
            }
        }

        content {
            showSelectorDialog = true
        }

        if (showSelectorDialog) {
            ImagePickerSelectorDialog(

                onGallerySelectClick = {
                    if (needToPickMultipleFiles && maxFiles > 1 && multiplePhotoPickerLauncher != null) {
                        launchMultipleImagePicker(multiplePhotoPickerLauncher)
                    } else {
                        launchImagePicker(pickerLauncher)
                    }
                },
                onCameraSelectClick = {
                    if (!cameraPermissionState.status.isGranted) {
                        cameraRequestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        launchCameraPicker(cameraLauncher)
                    }
                },
                onDismiss = { showSelectorDialog = false },
                modifier = dialogModifier,
                config = dialogConfig
            )
        }
    } else {
        content {}
    }
}

private fun validateFilesAndCompress(
    files: List<File>,
    onFileChanges: ((file: List<File>) -> Unit)?,
    context: Context,
    coroutineScope: CoroutineScope,
    showSnackbar: suspend (message: String) -> Unit,
    setIsCompressionProceed: (isUnderCompression: Boolean) -> Unit
) {
    val maxImageSize = 15 * 1024 * 1024
    val targetImageSize = 5 * 1000 * 1000

    val validFiles = mutableListOf<File>()
    val compressedFiles = mutableListOf<File>()

    coroutineScope.launch(Dispatchers.IO) {
        setIsCompressionProceed(true)

        files.forEach { file ->
            if (file.length() > maxImageSize) {
                showSnackbar("Photo must be less 15 mb")
            } else if (file.length() < targetImageSize) {
                validFiles.add(file)
            } else {
                try {
                    val compressedFile = CompressImageUtil.compressImage(file, context)
                    compressedFile?.let { compressedFiles.add(it) }
                        ?: showSnackbar("Something went wrong")
                } catch (e: Exception) {
                    showSnackbar("Something went wrong")
                    return@launch
                }
            }
        }

        validFiles.addAll(compressedFiles)
        onFileChanges?.invoke(validFiles)
        setIsCompressionProceed(false)
    }
}

private fun launchImagePicker(pickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
    pickerLauncher.launch(
        PickVisualMediaRequest(
            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
        )
    )
}

private fun launchCameraPicker(pickerLauncher: ManagedActivityResultLauncher<Unit, File?>) {
    pickerLauncher.launch(Unit)
}

private fun launchMultipleImagePicker(
    pickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>
) {
    pickerLauncher.launch(
        PickVisualMediaRequest(
            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
        )
    )
}