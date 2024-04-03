package com.urlaunched.android.design.ui.progressbarcontainer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.urlaunched.android.design.ui.progressbarcontainer.constants.ProgressBarContainerConstants

@Composable
fun ProgressBarContainer(
    isLoading: Boolean,
    indicatorColor: Color = ProgressIndicatorDefaults.circularColor,
    indicatorTrackColor: Color = ProgressIndicatorDefaults.circularTrackColor,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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