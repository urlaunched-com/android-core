package com.urlaunched.android.player.signleplayerstate.model

import com.urlaunched.android.player.models.AudioState

data class PlayerUiState(
    val audioState: AudioState,
    val currentMediaItemId: String,
    val audioDuration: Long,
    val isWaitingForAutoplay: Boolean = false
)