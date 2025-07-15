package com.urlaunched.android.design.ui.onboardingcontainer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.urlaunched.android.design.ui.onboardingcontainer.models.OnboardingContainerColor
import com.urlaunched.android.design.ui.onboardingcontainer.models.OnboardingContainerDimens
import com.urlaunched.android.design.ui.onboardingcontainer.models.StepProgressColors
import com.urlaunched.android.design.ui.onboardingcontainer.models.StepProgressDimens

@Composable
fun OnboardingContainer(
    activeStepIndex: Int,
    countOfSteps: Int,
    pagerState: PagerState,
    onboardingContainerColor: OnboardingContainerColor,
    onboardingContainerDimens: OnboardingContainerDimens = OnboardingContainerDimens(),
    stepProgressColors: StepProgressColors,
    stepProgressDimens: StepProgressDimens = StepProgressDimens(),
    imageContent: @Composable (page: Int) -> Unit,
    titleContent: @Composable () -> Unit,
    descriptionContent: @Composable () -> Unit,
    nextButtonContent: @Composable () -> Unit,
    skipButtonContent: @Composable () -> Unit,
    additionalContent: @Composable () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(onboardingContainerColor.backgroundColor)
            .systemBarsPadding()
            .padding(vertical = onboardingContainerDimens.verticalSpacing)
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
            state = pagerState
        ) { page ->
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                imageContent(page)

                titleContent()

                descriptionContent()
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(stepProgressDimens.progressBarTopSpacing))

            StepProgressBar(
                activeStepIndex = activeStepIndex,
                countOfSteps = countOfSteps,
                stepProgressColors = stepProgressColors,
                stepProgressDimens = stepProgressDimens
            )

            nextButtonContent()

            skipButtonContent()

            additionalContent()
        }
    }
}