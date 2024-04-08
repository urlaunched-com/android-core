package com.urlaunched.android.player.mediacontroller.models

import com.urlaunched.android.player.models.AudioState
import kotlin.time.Duration

data class MediaControllerUiState(
    val audioState: AudioState,
    val currentItemDuration: Duration,
    val currentAudioPosition: Duration,
    val currentAudioId: String?,
    val hasNextItem: Boolean,
    val hasPreviousItem: Boolean,
    val currentTrack: Track?,
    val playbackSpeed: PlaybackSpeed,
    val playlist: List<Track>
)