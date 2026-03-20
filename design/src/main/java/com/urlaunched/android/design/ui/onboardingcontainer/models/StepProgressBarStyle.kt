package com.urlaunched.android.design.ui.onboardingcontainer.models

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.ui.onboardingcontainer.constants.StepProgressBarDimens

data class StepProgressBarStyle(
    val stepHeight: Dp = StepProgressBarDimens.defaultStepHeight,
    val stepWidth: Dp = StepProgressBarDimens.defaultStepWidth,
    val stepsSpacing: Dp = StepProgressBarDimens.defaultStepsSpacing,
    val stepShape: Shape = CircleShape
)