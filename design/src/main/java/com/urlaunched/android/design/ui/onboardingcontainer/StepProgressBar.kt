package com.urlaunched.android.design.ui.onboardingcontainer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.ui.onboardingcontainer.constants.StepProgressBarConstants
import com.urlaunched.android.design.ui.onboardingcontainer.models.StepProgressBarColors
import com.urlaunched.android.design.ui.onboardingcontainer.models.StepProgressBarStyle

@Composable
fun StepProgressBar(
    modifier: Modifier = Modifier,
    activeStepIndex: Int,
    stepsCount: Int,
    stepProgressBarColors: StepProgressBarColors,
    stepProgressBarStyle: StepProgressBarStyle
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(stepProgressBarStyle.stepsSpacing)
    ) {
        for (stepIndex in StepProgressBarConstants.PROGRESS_BAR_FIRST_STEP until stepsCount) {
            val tabColor by animateColorAsState(
                targetValue = if (stepIndex == activeStepIndex) {
                    stepProgressBarColors.selectedStepColor
                } else {
                    stepProgressBarColors.unselectedStepColor
                },
                animationSpec = tween(StepProgressBarConstants.STEP_COLOR_ANIM_DURATION_MILLIS, easing = LinearEasing)
            )

            Box(
                modifier = Modifier
                    .size(height = stepProgressBarStyle.stepHeight, width = stepProgressBarStyle.stepWidth)
                    .clip(stepProgressBarStyle.stepShape)
                    .background(tabColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StepProgressBarPreview() {
    StepProgressBar(
        modifier = Modifier.padding(16.dp),
        activeStepIndex = 1,
        stepsCount = 4,
        stepProgressBarColors = StepProgressBarColors(),
        stepProgressBarStyle = StepProgressBarStyle()
    )
}