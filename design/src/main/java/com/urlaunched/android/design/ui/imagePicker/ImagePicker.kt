package com.urlaunched.android.design.ui.imagepicker

import android.Manifest
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.urlaunched.android.common.files.FileHelper
import com.urlaunched.android.common.files.TakeCameraPictureContract
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.image.UrlImage
import com.urlaunched.android.design.ui.imagepicker.model.ImagePickerDialogStyle
import com.urlaunched.android.design.ui.imagepicker.model.ImagePickerTextStyles
import com.urlaunched.android.design.ui.imagepicker.model.ImageSource
import com.urlaunched.android.design.ui.imagepicker.model.ImagesCount
import java.io.File

private const val FILE_PROVIDER = ".provider"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker(
    state: ImagePickerState,
    gallerySource: ImageSource.Gallery,
    cameraSource: ImageSource.Camera,
    headlineText: String,
    dismissButtonText: String,
    style: ImagePickerDialogStyle = ImagePickerDialogStyle(),
    textStyles: ImagePickerTextStyles = ImagePickerTextStyles()
) {
    if (!LocalInspectionMode.current) {
        val context = LocalContext.current
        val cameraPickerContract = remember {
            TakeCameraPictureContract(fileProviderAuthority = context.packageName + FILE_PROVIDER)
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = cameraPickerContract,
            onResult = { file ->
                if (file != null) {
                    state.processPickedFiles(files = listOf(file), context = context)
                }
            }
        )

        val pickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    state.processPickedUris(uris = listOf(uri), context = context)
                }
            }
        )

        val multiplePhotoPickerLauncher = if (state.maxImagesCount > ImagesCount.SINGLE_IMAGE) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = state.maxImagesCount.count),
                onResult = { uris ->
                    state.processPickedUris(uris = uris, context = context)
                }
            )
        } else {
            null
        }

        val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

        val cameraRequestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                if (granted) {
                    launchCameraPicker(cameraLauncher)
                }
            }
        )

        if (state.deleteTempFilesOnDispose) {
            DisposableEffect(Unit) {
                onDispose {
                    FileHelper.deleteTempFilesFromCache(context)
                }
            }
        }

        if (state.isSourceSelectorDialogShown) {
            ImageSourceSelectorDialog(
                onGalleryClick = {
                    if (state.maxImagesCount > ImagesCount.SINGLE_IMAGE && multiplePhotoPickerLauncher != null) {
                        launchMultipleImagePicker(multiplePhotoPickerLauncher)
                    } else {
                        launchImagePicker(pickerLauncher)
                    }
                },
                onCameraClick = {
                    if (!cameraPermissionState.status.isGranted) {
                        cameraRequestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        launchCameraPicker(cameraLauncher)
                    }
                },
                onDismiss = state::hideSourceSelectorDialog,
                cameraSource = cameraSource,
                gallerySource = gallerySource,
                headlineText = headlineText,
                dismissButtonText = dismissButtonText,
                style = style,
                textStyles = textStyles
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagePickerPreview() {
    var pickedImage by remember { mutableStateOf<List<File>>(emptyList()) }
    val imagePickerState = rememberImagePickerState(
        onError = {},
        onImagesPicked = { images ->
            pickedImage += images
        }
    )

    ImagePicker(
        state = imagePickerState,
        cameraSource = ImageSource.Camera(title = "Camera"),
        gallerySource = ImageSource.Gallery(title = "Gallery"),
        headlineText = "Choose source",
        dismissButtonText = "Cancel",
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = Dimens.spacingBig, alignment = Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = Dimens.spacingNormal, alignment = Alignment.CenterHorizontally)
        ) {
            pickedImage.forEach { image ->
                UrlImage(
                    model = image,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(Dimens.cornerRadiusNormal))
                )
            }
        }

        Button(
            enabled = pickedImage.size < 3,
            onClick = {
                imagePickerState.showSourceSelectorDialog(ImagesCount(count = 3 - pickedImage.size))
            }
        ) {
            Text("Pick images")
        }
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