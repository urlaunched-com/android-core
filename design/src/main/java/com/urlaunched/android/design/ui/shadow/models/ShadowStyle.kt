package com.urlaunched.android.design.ui.shadow.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

data class ShadowStyle(
    val color: Color = Color.Black,
    val alpha: Float = 1f,
    val cornersRadius: Dp = 0.dp,
    val blurRadius: Dp = 0.dp,
    val offset: DpOffset = DpOffset.Zero
)