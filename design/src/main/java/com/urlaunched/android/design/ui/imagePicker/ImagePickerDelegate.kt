package com.urlaunched.android.design.ui.imagePicker

import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface ImagePickerDelegate {
    val uiImagePickerStage: StateFlow<ImagePickerDelegateState>

    fun selectFile(file: File?)
}

data class ImagePickerDelegateState(
    val selectedFile: File?,
)