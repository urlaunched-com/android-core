package com.urlaunched.android.design.ui.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.urlaunched.android.design.ui.player.models.AudioPlayButtonColor
import com.urlaunched.android.design.ui.player.models.PlayerDimens

@Composable
fun Player(
    audioState: Any,
    playerDimens: PlayerDimens = PlayerDimens(),
    audioPlayButtonColor: AudioPlayButtonColor,
    onPlayAudioClick: () -> Unit,
    nextTrackButton: @Composable () -> Unit,
    rotatePlusButton: @Composable () -> Unit,
    previousTrackButton: @Composable () -> Unit,
    rotateMinusButton: @Composable () -> Unit,
    playingButton: @Composable () -> Unit,
    pauseButton: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = playerDimens.horizontalDimens, vertical = playerDimens.verticalDimens),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        nextTrackButton()

        rotateMinusButton()

        AudioPlayButton(
            audioState = audioState,
            onClick = onPlayAudioClick,
            playingButton = playingButton,
            pauseButton = pauseButton,
            audioPlayButtonColor = audioPlayButtonColor
        )

        rotatePlusButton()

        previousTrackButton()

    }
}