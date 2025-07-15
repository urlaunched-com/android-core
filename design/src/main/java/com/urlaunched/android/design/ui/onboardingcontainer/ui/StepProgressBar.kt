package com.urlaunched.android.design.ui.onboardingcontainer.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.urlaunched.android.design.ui.onboardingcontainer.models.StepProgressColors
import com.urlaunched.android.design.ui.onboardingcontainer.models.StepProgressDimens

@Composable
fun StepProgressBar(
    modifier: Modifier = Modifier,
    activeStepIndex: Int,
    countOfSteps: Int,
    stepProgressColors: StepProgressColors,
    stepProgressDimens: StepProgressDimens
) {
    Row(modifier = modifier) {
        for (step in 0..countOfSteps) {
            val tabTextColor = animateColorAsState(
                targetValue = if (step == activeStepIndex) {
                    stepProgressColors.selectedStepColor
                } else {
                    stepProgressColors.unselectedStepColor
                },
                animationSpec = tween(300, easing = LinearEasing)
            )

            Box(
                modifier = Modifier
                    .size(height = stepProgressDimens.containerHeight, width = stepProgressDimens.containerWidth)
                    .clip(RoundedCornerShape(stepProgressDimens.containerShape))
                    .background(tabTextColor.value)
            )

            Spacer(Modifier.width(stepProgressDimens.horizontalSpacer))
        }
    }
}

