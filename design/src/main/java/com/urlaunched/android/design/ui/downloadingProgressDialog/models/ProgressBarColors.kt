package com.urlaunched.android.design.ui.downloadingProgressDialog.models

import androidx.compose.ui.graphics.Color

data class ProgressBarColors(
    val progressColors: List<Color> = listOf(Color.Blue),
    val progressBackgroundColor: Color = Color.Gray,
    val backgroundDialogColor: Color = Color.White
)