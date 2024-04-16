package com.urlaunched.android.design.ui.textfield.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class TextFieldBottomLabelConfig(
    val focusedColor: Color = Color.Black,
    val unfocusedColor: Color = focusedColor,
    val textStyle: TextStyle = TextStyle.Default
)