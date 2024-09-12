package com.urlaunched.android.design.ui.progressbarcontainer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.ui.progressbarcontainer.constants.ProgressBarContainerConstants

@Composable
fun ProgressBarContainer(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    indicatorColor: Color = ProgressIndicatorDefaults.circularColor,
    indicatorTrackColor: Color = ProgressIndicatorDefaults.circularTrackColor,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .clickable(enabled = false, onClick = {})
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = ProgressBarContainerConstants.CONTAINER_COLOR_ALPHA)
                    )
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = indicatorColor,
                    trackColor = indicatorTrackColor
                )
            }
        }
    }
}

@Composable
fun ProgressBarContainer(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    indicatorColor: Color = ProgressIndicatorDefaults.circularColor,
    indicatorTrackColor: Color = ProgressIndicatorDefaults.circularTrackColor,
    progress: Float = 0F,
    progressContent: @Composable (Float) -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                if (progress > 1) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = indicatorColor,
                        trackColor = indicatorTrackColor
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        progress = { progress },
                        color = indicatorColor,
                        trackColor = indicatorTrackColor
                    )
                    progressContent(progress)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenProgressBarPreview() {
    ProgressBarContainer(
        isLoading = true,
        content = {
            Text(text = "Test text")
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun FullScreenProgressBarWithPercentagePreview() {
    ProgressBarContainer(
        isLoading = true,
        progress = 0.5F,
        progressContent = { progress ->
            Text(
                text = "${(progress * 100).toInt()}%",
                style = Typography().labelMedium,
                color = Color.White
            )
            Text(
                text = "Loading, please wait...",
                modifier = Modifier.padding(top = 90.dp),
                style = Typography().labelLarge,
                color = Color.White
            )
        },
        content = {
            Text(text = "Test text")
        }
    )
}