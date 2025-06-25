package com.urlaunched.android.design.ui.player.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.urlaunched.android.design.ui.player.models.AudioPlayButtonConstants
import com.urlaunched.android.design.ui.player.models.AudioPlayButtonDimens
import com.urlaunched.android.design.ui.player.models.AudioPlayButtonColor
import com.urlaunched.android.design.ui.player.models.AudioStateType

@Composable
internal fun AudioPlayButton(
    modifier: Modifier = Modifier,
    audioState: Any,
    audioPlayButtonDimens: AudioPlayButtonDimens = AudioPlayButtonDimens(),
    audioPlayButtonColor: AudioPlayButtonColor = AudioPlayButtonColor(),
    audioPlayButtonConstants: AudioPlayButtonConstants = AudioPlayButtonConstants(),
    onClick: () -> Unit,
    playingButton: @Composable () -> Unit,
    pauseButton: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .size(audioPlayButtonDimens.buttonSize)
            .background(
                color = audioPlayButtonColor.buttonColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = audioState,
            contentAlignment = Alignment.Center,
            label = audioPlayButtonConstants.audioPlayButtonIconAnimationLabel
        ) { state ->
            val name = (state as? Enum<*>)?.name

            when (name) {
                AudioStateType.PAUSE.name -> playingButton()

                AudioStateType.PLAYING.name -> pauseButton()

                AudioStateType.BUFFERING.name -> CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(audioPlayButtonConstants.playButtonProgressSizeFraction),
                    trackColor = Color.Transparent,
                    color = audioPlayButtonColor.circularProgressColor
                )
            }
        }
    }
}