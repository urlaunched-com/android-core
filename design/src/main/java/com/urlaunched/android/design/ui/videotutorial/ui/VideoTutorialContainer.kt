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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.urlaunched.android.common.lifecycle.HandleLifecycleEvents
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.videotutorial.model.VideoProgressColors
import com.urlaunched.android.design.ui.videotutorial.model.VideoProgressStyle
import com.urlaunched.android.player.signleplayerstate.PlayerCollectingContainer
import com.urlaunched.android.player.signleplayerstate.model.PlayerUiState
import com.urlaunched.android.player.signleplayerstate.rememberPlayerState
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
@Composable
fun VideoTutorialContainer(
    modifier: Modifier = Modifier,
    videoUrls: List<String>,
    onTutorialFinish: () -> Unit,
    videoResizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
    progressBarPadding: PaddingValues =
        PaddingValues(horizontal = Dimens.spacingNormal, vertical = Dimens.spacingNormalSpecial),
    videoProgressColors: VideoProgressColors = VideoProgressColors(),
    videoProgressStyle: VideoProgressStyle = VideoProgressStyle(),
    closeButton: @Composable RowScope.() -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val playerState = rememberPlayerState(seekToStartOnEnd = false)
    val playerUiState by playerState.playerUiState.collectAsStateWithLifecycle()
    var currentMediaProgress by remember { mutableFloatStateOf(0f) }

    VideoTutorialContainer(
        modifier = modifier,
        player = playerState.player,
        mediaCount = videoUrls.size,
        currentMediaIndex = playerUiState.currentMediaIndex,
        currentMediaProgress = currentMediaProgress,
        videoResizeMode = videoResizeMode,
        progressBarPadding = progressBarPadding,
        videoProgressStyle = videoProgressStyle,
        videoProgressColors = videoProgressColors,
        onPreviousVideo = {
            if (playerState.player?.hasPreviousMediaItem() == true) {
                playerState.player?.seekToPreviousMediaItem()
            } else {
                playerState.player?.seekToPrevious()
            }
        },
        onNextVideo = {
            playerState.player?.seekToNext()
        },
        closeButton = closeButton
    )

    PlayerCollectingContainer(
        playerUiState = playerUiState,
        getCurrentPosition = {
            playerState.currentPlayingPosition
        },
        content = { _: PlayerUiState, _: Long, currentPlayingPosition: MutableState<Long>, duration: Long ->
            currentMediaProgress = (currentPlayingPosition.value.milliseconds / duration.milliseconds).toFloat()
                .takeIf { it.isFinite() } ?: 0f
        }
    )

    LaunchedEffect(playerUiState.endReached) {
        if (playerUiState.endReached) {
            onTutorialFinish()
        }
    }

    HandleLifecycleEvents(
        onStart = { playerState.player?.play() }
    )

    DisposableEffect(playerState) {
        lifecycleOwner.lifecycle.addObserver(playerState)
        playerState.playUrls(videoUrls)

        onDispose {
            playerState.release()
            lifecycleOwner.lifecycle.removeObserver(playerState)
        }
    }
}

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
        videoUrls = listOf(
            "https://www.w3schools.com/html/mov_bbb.mp4",
            "https://www.w3schools.com/html/mov_bbb.mp4",
            "https://www.w3schools.com/html/mov_bbb.mp4",
            "https://www.w3schools.com/html/mov_bbb.mp4"
        ),
        onTutorialFinish = {},
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