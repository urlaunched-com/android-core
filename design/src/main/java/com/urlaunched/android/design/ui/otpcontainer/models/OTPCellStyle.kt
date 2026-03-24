package com.urlaunched.android.design.ui.otpcontainer.models

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.otpcontainer.constants.OTPCellDimens
import com.urlaunched.android.design.ui.shadow.models.ShadowStyle

data class OTPCellStyle(
    val shape: Shape = RoundedCornerShape(Dimens.cornerRadiusSmall),
    val width: Dp = OTPCellDimens.defaultOtpCellWidth,
    val height: Dp = OTPCellDimens.defaultOtpCellHeight,
    val focusedBorderWidth: Dp = OTPCellDimens.defaultBorderWidth,
    val unfocusedBorderWidth: Dp = OTPCellDimens.defaultBorderWidth,
    val shadowStyle: ShadowStyle? = null
)