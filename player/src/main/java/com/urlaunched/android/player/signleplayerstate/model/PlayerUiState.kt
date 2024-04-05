package com.urlaunched.android.player.signleplayerstate.model

data class PlayerUiState(
    val audioState: AudioState,
    val currentMediaItemId: String,
    val audioDuration: Long,
    val isWaitingForAutoplay: Boolean = false
)