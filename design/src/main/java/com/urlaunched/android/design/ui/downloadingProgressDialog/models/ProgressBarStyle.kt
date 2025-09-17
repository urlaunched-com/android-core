package com.urlaunched.android.design.ui.downloadingProgressDialog.models

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.ui.downloadingProgressDialog.constants.DownloadingProgressDialogDimens

data class ProgressBarStyle(
    val progressBarHeight: Dp = DownloadingProgressDialogDimens.defaultProgressBarHeight,
    val trackBrush: Brush,
    val progressBrush: Brush,
    val trackShape: Shape = CircleShape,
    val progressShape: Shape = trackShape
) {
    constructor(
        progressBarHeight: Dp = DownloadingProgressDialogDimens.defaultProgressBarHeight,
        trackColor: Color = Color.LightGray,
        progressColor: Color = Color.Blue,
        trackShape: Shape = CircleShape,
        progressShape: Shape = trackShape
    ) : this(
        progressBarHeight = progressBarHeight,
        trackBrush = SolidColor(trackColor),
        progressBrush = SolidColor(progressColor),
        trackShape = trackShape,
        progressShape = progressShape
    )
}