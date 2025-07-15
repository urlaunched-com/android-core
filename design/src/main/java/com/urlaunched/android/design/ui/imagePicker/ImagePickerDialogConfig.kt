package com.urlaunched.android.design.ui.imagePicker

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class ImagePickerDialogConfig(
    val headerText: String,
    val cameraText: String,
    val galleryText: String,
    val onDismissText: String = "Cancel",

    val headerTextStyle: TextStyle,
    val labelTextStyle: TextStyle,
    val cancelTextStyle: TextStyle,

    val iconCamera: @Composable () -> Unit,
    val iconGallery: @Composable () -> Unit,

    val containerColor: Color,
    val headerTextColor: Color,
    val labelTextColor: Color,
    val cancelTextColor: Color
)
