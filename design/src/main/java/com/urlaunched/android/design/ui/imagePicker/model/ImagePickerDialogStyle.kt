package com.urlaunched.android.design.ui.imagepicker.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.ui.imagepicker.dimens.ImagePickerDimens

data class ImagePickerDialogStyle(
    val shape: Shape = RoundedCornerShape(ImagePickerDimens.imagePickerDialogCornerRadius),
    val containerColor: Color = Color.White,
    val contentPadding: PaddingValues = PaddingValues(ImagePickerDimens.imagePickerDialogContentPadding),
    val contentSpacing: Dp = ImagePickerDimens.imagePickerDialogContentSpacing
)