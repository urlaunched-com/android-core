package com.urlaunched.android.design.ui.imagepicker.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.urlaunched.android.design.R

sealed class ImageSource {
    @get:DrawableRes
    abstract val iconResId: Int
    abstract val title: String
    abstract val contentDescription: String?
    abstract val iconTint: Color

    data class Camera(
        override val title: String,
        @DrawableRes
        override val iconResId: Int = R.drawable.ic_camera,
        override val contentDescription: String? = title,
        override val iconTint: Color = Color.Unspecified
    ) : ImageSource()

    data class Gallery(
        override val title: String,
        @DrawableRes
        override val iconResId: Int = R.drawable.ic_gallery,
        override val contentDescription: String? = title,
        override val iconTint: Color = Color.Unspecified
    ) : ImageSource()
}