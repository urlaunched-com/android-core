package com.urlaunched.android.design.ui.textfield.models

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.urlaunched.android.design.ui.textfield.constants.TextFieldDimens

data class TextFieldBackgroundConfig(
    val focusedColor: Color = Color.Transparent,
    val unfocusedColor: Color = focusedColor,
    val shape: Shape = RoundedCornerShape(TextFieldDimens.cornersRadius)
)