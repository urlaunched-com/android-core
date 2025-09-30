package com.urlaunched.android.design.ui.otpcontainer.constants

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

object OTPContainerDefaults {
    val DefaultKeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.NumberPassword,
        imeAction = ImeAction.Done
    )
}