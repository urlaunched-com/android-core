package com.urlaunched.android.design.ui.otpcontainer.models

import androidx.compose.ui.graphics.Color

data class OTPCellColors(
    val backgroundColor: Color = Color.White,
    val emptyBackgroundColor: Color = backgroundColor,
    val focusedBackgroundColor: Color = backgroundColor,
    val borderColor: Color = Color.LightGray,
    val emptyBorderColor: Color = borderColor,
    val focusedBorderColor: Color = borderColor,
    val errorBorderColor: Color = Color.Red
)