package com.urlaunched.android.design.ui.otptextfield.constants

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

object OtpTextFieldDefaults {

    val DefaultKeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number
    )
}