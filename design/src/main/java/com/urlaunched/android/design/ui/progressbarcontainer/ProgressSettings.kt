package com.urlaunched.android.design.ui.progressbarcontainer

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class ProgressSettings(
    val mainTextStyle: TextStyle = Typography().labelMedium,
    val mainColor: Color = Color.White,
    val description: String? = "Loading, please wait...",
    val descriptionTextStyle: TextStyle = Typography().labelLarge,
    val descriptionColor: Color = Color.White
)