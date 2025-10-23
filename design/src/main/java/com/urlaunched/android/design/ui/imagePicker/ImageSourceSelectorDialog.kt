package com.urlaunched.android.design.ui.imagepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.urlaunched.android.design.ui.clickable.debouncedClickable
import com.urlaunched.android.design.ui.imagepicker.dimens.ImagePickerDimens
import com.urlaunched.android.design.ui.imagepicker.model.ImagePickerDialogStyle
import com.urlaunched.android.design.ui.imagepicker.model.ImagePickerTextStyles
import com.urlaunched.android.design.ui.imagepicker.model.ImageSource

@Composable
internal fun ImageSourceSelectorDialog(
    onDismiss: () -> Unit,
    gallerySource: ImageSource.Gallery,
    cameraSource: ImageSource.Camera,
    headlineText: String,
    dismissButtonText: String,
    style: ImagePickerDialogStyle = ImagePickerDialogStyle(),
    textStyles: ImagePickerTextStyles = ImagePickerTextStyles(),
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .background(style.containerColor, style.shape)
                .padding(style.contentPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = headlineText,
                style = textStyles.headlineStyle
            )

            Spacer(modifier = Modifier.height(style.contentSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageSourceOption(
                    modifier = Modifier.weight(1f),
                    textStyle = textStyles.optionTextStyle,
                    imageSource = cameraSource,
                    onClick = {
                        onCameraClick()
                        onDismiss()
                    }
                )

                ImageSourceOption(
                    modifier = Modifier.weight(1f),
                    textStyle = textStyles.optionTextStyle,
                    imageSource = gallerySource,
                    onClick = {
                        onGalleryClick()
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(style.contentSpacing))

            DismissButton(
                text = dismissButtonText,
                textStyle = textStyles.dismissButtonStyle,
                modifier = Modifier.align(Alignment.End),
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun DismissButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = TextStyle(),
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ImagePickerDimens.dismissButtonCornerRadius))
            .debouncedClickable(onClick = onClick)
            .padding(ImagePickerDimens.dismissButtonInnerPadding)
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
private fun ImageSourceOption(
    modifier: Modifier = Modifier,
    imageSource: ImageSource,
    textStyle: TextStyle = TextStyle(),
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(ImagePickerDimens.imageSourceCornerRadius))
                .debouncedClickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(imageSource.iconResId),
                contentDescription = imageSource.contentDescription,
                modifier = Modifier.size(ImagePickerDimens.imageSourceOptionsIconSize),
                tint = imageSource.iconTint
            )
        }

        Spacer(modifier = Modifier.height(ImagePickerDimens.imageSourceTitleSpacing))

        Text(
            text = imageSource.title,
            style = textStyle
        )
    }
}

@Preview
@Composable
private fun ImageSourceSelectorDialogPreview() {
    ImageSourceSelectorDialog(
        onDismiss = {},
        cameraSource = ImageSource.Camera(title = "Camera"),
        gallerySource = ImageSource.Gallery(title = "Gallery"),
        headlineText = "Choose source",
        dismissButtonText = "Cancel",
        onGalleryClick = {},
        onCameraClick = {}
    )
}
