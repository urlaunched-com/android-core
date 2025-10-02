package com.urlaunched.android.design.ui.downloadingprogressdialog.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class ProgressTextStyle(
    val textStyle: TextStyle = TextStyle(),
    val startColor: Color = Color.White,
    val endColor: Color = Color.Gray
)