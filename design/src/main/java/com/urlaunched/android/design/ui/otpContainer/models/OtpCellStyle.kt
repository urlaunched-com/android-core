package com.urlaunched.android.design.ui.otpContainer.models

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.otpContainer.constants.OtpContainerDimens

data class OtpCellStyle(
    val textStyle: TextStyle = androidx.compose.material3.Typography().bodyLarge,
    val shape: Shape = RoundedCornerShape(Dimens.cornerRadiusSmall),
    val width: Dp = OtpContainerDimens.defaultOtpCellWidth,
    val height: Dp = OtpContainerDimens.defaultOtpCellHeight,
    val focusedBorderWidth: Dp = OtpContainerDimens.defaultBorderWidth,
    val unfocusedBorderWidth: Dp = OtpContainerDimens.defaultBorderWidth,
    val colors: OtpCellColors = OtpCellColors()
)