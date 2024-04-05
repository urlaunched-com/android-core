package com.urlaunched.android.player.signleplayerstate

import androidx.media3.common.Player
import com.urlaunched.android.player.signleplayerstate.model.AudioState
import com.urlaunched.android.player.signleplayerstate.model.PlayerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NoOpSinglePlayerStateImpl : SinglePlayerState {
    override val playerUiState: StateFlow<PlayerUiState> = MutableStateFlow(
        PlayerUiState(
            audioState = AudioState.PAUSE,
            currentMediaItemId = "",
            audioDuration = 0L
        )
    )
    override val currentPlayingPosition: Long = 0
    override val player: Player? = null

    override fun seekTo(millis: Long) {
        // No-op
    }

    override fun seekFor(millis: Long) {
        // No-op
    }

    override fun playUrl(url: String, id: String) {
        // No-op
    }

    override fun playFile(path: String, id: String) {
        // No-op
    }

    override fun pause() {
        // No-op
    }

    override fun release() {
        // No-op
    }
}