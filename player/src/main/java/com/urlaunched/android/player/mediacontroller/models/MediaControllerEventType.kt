package com.urlaunched.android.player.mediacontroller.models

import com.urlaunched.android.player.models.AudioState
import kotlin.time.Duration

internal sealed class MediaControllerEventType {
    data class AudioStateEvent(val audioState: AudioState) : MediaControllerEventType()
    data class PositionEvent(val currentItemDuration: Duration, val currentAudioPosition: Duration) :
        MediaControllerEventType()

    data class MediaTransitionEvent(
        val currentAudioId: String?,
        val playlistPosition: Int,
        val hasNextItem: Boolean,
        val hasPreviousItem: Boolean
    ) : MediaControllerEventType()

    data class PlaybackSpeedEvent(val playbackSpeed: PlaybackSpeed) : MediaControllerEventType()
}