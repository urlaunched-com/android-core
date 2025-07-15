package com.urlaunched.android.design.ui.onboardingcontainer.models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens

data class StepProgressDimens(
    val containerHeight: Dp = 5.dp,
    val containerWidth: Dp = 24.dp,
    val containerShape: Dp = 6.dp,
    val horizontalSpacer: Dp = 3.dp,
    val progressBarTopSpacing: Dp = Dimens.spacingLarge
)