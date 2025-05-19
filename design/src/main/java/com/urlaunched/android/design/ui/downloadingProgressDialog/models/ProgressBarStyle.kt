package com.urlaunched.android.design.ui.downloadingProgressDialog.models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ProgressBarStyle(
    val progressBarMinWidth: Dp = 32.dp,
    val progressBarHeight: Dp = 36.dp,
    val progressBoxCornerRadius: Dp = 32.dp,
    val progressCornerRadius: Dp = 32.dp,
    val dialogCornerRadius: Dp = 24.dp
)