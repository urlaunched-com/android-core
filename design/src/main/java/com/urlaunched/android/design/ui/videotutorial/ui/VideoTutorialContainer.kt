package com.urlaunched.android.design.ui.videotutorial.ui

import androidx.annotation.FloatRange
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.videotutorial.model.VideoProgressColors
import com.urlaunched.android.design.ui.videotutorial.model.VideoProgressStyle

@OptIn(UnstableApi::class)
@Composable
fun VideoTutorialContainer(
    modifier: Modifier = Modifier,
    player: Player?,
    mediaCount: Int,
    currentMediaIndex: Int,
    @FloatRange(0.0, 1.0)
    currentMediaProgress: Float,
    onPreviousVideo: () -> Unit,
    onNextVideo: () -> Unit,
    videoResizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
    progressBarPadding: PaddingValues =
        PaddingValues(horizontal = Dimens.spacingNormal, vertical = Dimens.spacingNormalSpecial),
    videoProgressColors: VideoProgressColors = VideoProgressColors(),
    videoProgressStyle: VideoProgressStyle = VideoProgressStyle(),
    closeButton: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        VideoPlayer(
            videoPlayer = player,
            videoResizeMode = videoResizeMode
        )

        VideoTapNavigator(
            onPreviousVideo = onPreviousVideo,
            onNextVideo = onNextVideo
        )

        VideoProgress(
            modifier = Modifier.padding(progressBarPadding),
            mediaCount = mediaCount,
            currentMediaIndex = currentMediaIndex,
            currentMediaProgress = currentMediaProgress,
            progressColor = videoProgressColors.progressColor,
            trackColor = videoProgressColors.trackColor,
            closeButton = closeButton,
            shadow = videoProgressStyle.shadow,
            gapSize = videoProgressStyle.gapSize
        )
    }
}

@Preview
@Composable
private fun VideoTutorialContainerPreview() {
    VideoTutorialContainer(
        player = null,
        mediaCount = 5,
        currentMediaIndex = 2,
        currentMediaProgress = 0.6f,
        onPreviousVideo = {},
        onNextVideo = {},
        closeButton = {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(start = Dimens.spacingNormal)
                    .clickable(
                        interactionSource = null,
                        indication = ripple(bounded = false),
                        onClick = {}
                    )
            )
        }
    )
}