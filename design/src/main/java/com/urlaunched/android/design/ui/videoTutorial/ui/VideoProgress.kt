package com.urlaunched.android.design.ui.videoTutorial.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.videoTutorial.model.VideoProgressColors
import com.urlaunched.android.design.ui.videoTutorial.model.VideoProgressPadding

@Composable
internal fun VideoProgress(
    mediaLink: List<String>,
    currentMediaIndex: Int,
    progressForCurrentMedia: Float,
    videoProgressDefaultColors: VideoProgressColors = VideoProgressColors(),
    videoProgressPadding: VideoProgressPadding = VideoProgressPadding(),
    closeButton: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(
                top = videoProgressPadding.top,
                start = videoProgressPadding.start,
                end = videoProgressPadding.end
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(mediaLink.size) { index ->
            LinearProgressIndicator(
                modifier = Modifier
                    .dropShadow(cornersRadius = Dimens.cornerRadiusLarge)
                    .weight(1f)
                    .padding(end = if (index != mediaLink.lastIndex) videoProgressPadding.spaceBetweenProgress else Dimens.zeroDp),
                strokeCap = StrokeCap.Round,
                trackColor = videoProgressDefaultColors.trackColor,
                color = videoProgressDefaultColors.progressColor,
                gapSize = Dimens.zeroDp,
                drawStopIndicator = {
                    // Do nothing
                },
                progress = {
                    when {
                        currentMediaIndex > index -> 1f
                        currentMediaIndex == index -> progressForCurrentMedia
                        else -> 0f
                    }
                }
            )
        }

        closeButton()
    }
}