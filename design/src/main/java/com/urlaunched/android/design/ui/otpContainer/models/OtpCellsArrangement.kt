package com.urlaunched.android.design.ui.otpContainer.models

import androidx.compose.ui.unit.Dp

sealed class OtpCellsArrangement {
    data class Spaced(val spacing: Dp) : OtpCellsArrangement()
    data object SpaceBetween : OtpCellsArrangement()
}