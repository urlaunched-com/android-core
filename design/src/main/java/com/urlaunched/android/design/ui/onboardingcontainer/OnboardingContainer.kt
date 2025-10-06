package com.urlaunched.android.design.ui.onboardingcontainer

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.onboardingcontainer.constants.OnboardingConstants
import com.urlaunched.android.design.ui.onboardingcontainer.models.StepProgressBarColors
import com.urlaunched.android.design.ui.onboardingcontainer.models.StepProgressBarStyle
import kotlinx.coroutines.launch

private const val FIRST_PAGE_INDEX = 0
private const val ONE_PAGE = 1

@Composable
fun <T> OnboardingContainer(
    modifier: Modifier = Modifier,
    pages: List<T>,
    initialPageIndex: Int = FIRST_PAGE_INDEX,
    stepProgressBarColors: StepProgressBarColors = StepProgressBarColors(),
    stepProgressBarStyle: StepProgressBarStyle = StepProgressBarStyle(),
    stepProgressPadding: PaddingValues = PaddingValues(top = Dimens.spacingNormal),
    contentArrangement: Arrangement.Vertical = Arrangement.Top,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onPageChange: ((page: T) -> Unit)? = null,
    nextButton: @Composable ColumnScope.(page: T, isLastPage: Boolean, nextPage: () -> Unit) -> Unit,
    skipButton: @Composable ColumnScope.(isLastPage: Boolean) -> Unit,
    additionalContent: @Composable ColumnScope.() -> Unit = {},
    pageContent: @Composable ColumnScope.(page: T) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = initialPageIndex,
        pageCount = { pages.size }
    )

    onPageChange?.let { onChange ->
        LaunchedEffect(pagerState.currentPage) {
            onChange(pages[pagerState.currentPage])
        }
    }

    val goToNextPage: () -> Unit = {
        coroutineScope.launch {
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + ONE_PAGE,
                animationSpec = TweenSpec(durationMillis = OnboardingConstants.PAGE_ANIMATION_DURATION_MILLIS)
            )
        }
    }

    OnboardingContainer(
        modifier = modifier,
        pagerState = pagerState,
        stepProgressBarColors = stepProgressBarColors,
        stepProgressBarStyle = stepProgressBarStyle,
        stepProgressPadding = stepProgressPadding,
        contentArrangement = contentArrangement,
        contentAlignment = contentAlignment,
        pageContent = { pageIndex ->
            pageContent(pages[pageIndex])
        },
        nextButton = {
            nextButton(pages[pagerState.currentPage], pagerState.currentPage == pages.lastIndex, goToNextPage)
        },
        skipButton = {
            skipButton(pagerState.currentPage == pages.lastIndex)
        },
        additionalContent = additionalContent
    )
}

@Composable
fun OnboardingContainer(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    stepProgressBarColors: StepProgressBarColors = StepProgressBarColors(),
    stepProgressBarStyle: StepProgressBarStyle = StepProgressBarStyle(),
    stepProgressPadding: PaddingValues = PaddingValues(top = Dimens.spacingNormal),
    contentArrangement: Arrangement.Vertical = Arrangement.Top,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    pageContent: @Composable ColumnScope.(page: Int) -> Unit,
    nextButton: @Composable ColumnScope.() -> Unit,
    skipButton: @Composable ColumnScope.() -> Unit,
    additionalContent: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
            state = pagerState
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = contentAlignment,
                verticalArrangement = contentArrangement
            ) {
                pageContent(page)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepProgressBar(
                activeStepIndex = pagerState.currentPage,
                stepsCount = pagerState.pageCount,
                stepProgressBarColors = stepProgressBarColors,
                stepProgressBarStyle = stepProgressBarStyle,
                modifier = Modifier.padding(stepProgressPadding)
            )

            nextButton()

            skipButton()

            additionalContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContainerPreview() {
    val pagerState = rememberPagerState(pageCount = { 4 })

    OnboardingContainer(
        pagerState = pagerState,
        pageContent = { page ->
            Box(
                modifier = Modifier
                    .padding(top = 100.dp)
                    .size(270.dp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(24.dp))
            )

            Text(
                text = "Welcome to the App",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 32.dp)
            )

            Text(
                text = "Your go-to application for everything.\nEmbark on an exciting journey with us.",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = Color(0xA3000000),
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        nextButton = {
            Button(
                onClick = {},
                modifier = Modifier
                    .padding(top = Dimens.spacingBig)
                    .width(200.dp)
            ) {
                Text("Next")
            }
        },
        stepProgressBarColors = StepProgressBarColors(
            selectedStepColor = MaterialTheme.colorScheme.primary,
            unselectedStepColor = MaterialTheme.colorScheme.primaryContainer
        ),
        skipButton = {
            TextButton(
                onClick = {},
                modifier = Modifier.width(200.dp)
            ) {
                Text("Skip")
            }
        },
        additionalContent = {
            Text(
                text = "Privacy Policy, Terms & Conditions",
                modifier = Modifier.padding(vertical = Dimens.spacingNormal)
            )
        }
    )
}