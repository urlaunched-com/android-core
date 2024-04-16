package com.urlaunched.android.design.ui.textfield.models

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.ui.textfield.constants.TextFieldDimens

data class TextFieldBorderConfig(
    val focusedColor: Color = Color.Black,
    val unfocusedColor: Color = focusedColor,
    val errorColor: Color? = null,
    val width: Dp = TextFieldDimens.borderSize,
    val shape: Shape = RoundedCornerShape(TextFieldDimens.cornersRadius)
)