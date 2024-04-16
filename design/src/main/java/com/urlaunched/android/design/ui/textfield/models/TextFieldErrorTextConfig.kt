package com.urlaunched.android.design.ui.textfield.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class TextFieldErrorTextConfig(
    val focusedColor: Color = Color.Red,
    val unfocusedColor: Color = focusedColor,
    val textStyle: TextStyle = TextStyle.Default
)