package com.urlaunched.android.design.ui.videoTutorial.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player

import com.urlaunched.android.design.ui.videoTutorial.model.VideoContentColors
import com.urlaunched.android.design.ui.videoTutorial.model.VideoProgressColors
import com.urlaunched.android.design.ui.videoTutorial.model.VideoProgressPadding

@Composable
fun VideoTutorialWrapper(
    modifier: Modifier = Modifier,
    player: Player?,
    videoContentColors: VideoContentColors = VideoContentColors(),
    onPreviousVideo: () -> Unit,
    onNextVideo: () -> Unit,
    mediaLink: List<String>,
    currentMediaIndex: Int,
    progressForCurrentMedia: Float,
    videoProgressDefaultColors: VideoProgressColors = VideoProgressColors(),
    videoProgressPadding: VideoProgressPadding = VideoProgressPadding(),
    closeButton: @Composable () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        VideoContent(
            player = player,
            videoContentColors = videoContentColors
        )

        VideoTapNavigator(
            onPreviousVideo = onPreviousVideo,
            onNextVideo = onNextVideo
        )

        VideoProgress(
            mediaLink = mediaLink,
            currentMediaIndex = currentMediaIndex,
            progressForCurrentMedia = progressForCurrentMedia,
            videoProgressDefaultColors = videoProgressDefaultColors,
            videoProgressPadding = videoProgressPadding,
            closeButton = closeButton,
        )
    }
}