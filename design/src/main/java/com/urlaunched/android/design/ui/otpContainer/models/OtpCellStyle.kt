package com.urlaunched.android.design.ui.otpContainer.models

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OtpCellStyle(
    val textStyle: TextStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
    val shape: Shape = RoundedCornerShape(8.dp),
    val width: Dp = 40.dp,
    val height: Dp = 56.dp,
    val backgroundColor: Color = Color.White,
    val emptyBackgroundColor: Color = Color.White,
    val filledBorderColor: Color = Color.LightGray,
    val focusedBorderWidth: Dp = 1.dp,
    val unfocusedBorderWidth: Dp = 1.dp,
    val focusedBorderColor: Color = Color.Black,
    val errorBorderColor: Color = Color.Red,
    val emptyBorderColor: Color = Color.White,
)