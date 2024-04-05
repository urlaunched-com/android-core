package com.urlaunched.android.player.signleplayerstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.urlaunched.android.player.signleplayerstate.model.AudioState
import com.urlaunched.android.player.signleplayerstate.model.PlayerUiState
import kotlinx.coroutines.delay

@Composable
fun PlayerCollectingContainer(
    playerUiState: PlayerUiState,
    getCurrentPosition: () -> Long,
    // This flag means whether the container should support
    // the function of seek by playback track in pause state
    seekPauseSupport: Boolean = false,
    content: @Composable (
        playerUiState: PlayerUiState,
        frequencyMs: Long,
        currentPlayingPosition: MutableState<Long>,
        duration: Long
    ) -> Unit
) {
    val currentPlayingPositionSlider = remember { mutableStateOf(getCurrentPosition()) }
    val duration = playerUiState.audioDuration

    content(playerUiState, FREQUENCY_MS, currentPlayingPositionSlider, duration)

    LaunchedEffect(key1 = playerUiState.audioState) {
        if (!seekPauseSupport) {
            while (playerUiState.audioState == AudioState.PLAYING) {
                delay(FREQUENCY_MS)
                currentPlayingPositionSlider.value = getCurrentPosition()
            }
        } else {
            while (true) {
                delay(FREQUENCY_MS)
                currentPlayingPositionSlider.value = getCurrentPosition()
            }
        }
    }
}

private const val FREQUENCY_MS = 10L