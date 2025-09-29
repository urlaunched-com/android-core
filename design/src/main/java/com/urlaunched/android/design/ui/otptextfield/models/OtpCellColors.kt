package com.urlaunched.android.design.ui.otptextfield.models

import androidx.compose.ui.graphics.Color

data class OtpCellColors(
    val backgroundColor: Color = Color.White,
    val emptyBackgroundColor: Color = Color.White,
    val filledBorderColor: Color = Color.LightGray,
    val focusedBorderColor: Color = Color.Black,
    val errorBorderColor: Color = Color.Red,
    val emptyBorderColor: Color = Color.White
)