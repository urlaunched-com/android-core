package com.urlaunched.android.design.ui.textfield.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class TextFieldInputTextConfig(
    val focusedColor: Color = Color.Black,
    val unfocusedColor: Color = focusedColor,
    val errorColor: Color? = Color.Red,
    val textStyle: TextStyle = TextStyle.Default
)