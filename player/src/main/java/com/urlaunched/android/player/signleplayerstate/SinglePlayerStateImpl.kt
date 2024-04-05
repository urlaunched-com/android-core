package com.urlaunched.android.player.signleplayerstate

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.urlaunched.android.player.models.AudioState
import com.urlaunched.android.player.signleplayerstate.model.PlayerUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

@UnstableApi
class SinglePlayerStateImpl(
    context: Context,
    private val coroutineScope: CoroutineScope,
    private val notificationData: NotificationPlayerHelper.NotificationData?,
    private val notificationChannelName: String?
) : SinglePlayerState {
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var mediaSessionCompat: MediaSessionCompat? = null

    override var player: Player? = ExoPlayer.Builder(context).build()

    private val _playerUiState = MutableStateFlow(
        PlayerUiState(
            audioState = AudioState.PAUSE,
            currentMediaItemId = "-1",
            audioDuration = 0
        )
    )

    private var actualState: AudioState = _playerUiState.value.audioState

    init {
        notificationData?.let {
            mediaSessionCompat = NotificationPlayerHelper.initializerMediaSessionCompat(context)

            mediaSessionCompat?.let { mediaSessionCompat ->
                playerNotificationManager =
                    NotificationPlayerHelper.initializePlayerNotificationManager(
                        context = context,
                        mediaSessionCompat = mediaSessionCompat,
                        player = player,
                        notificationData = notificationData,
                        notificationChannelName = notificationChannelName.orEmpty()
                    )
            }
        }
    }

    override val playerUiState: StateFlow<PlayerUiState> = _playerUiState

    override val currentPlayingPosition: Long get() = player?.currentPosition ?: 0L

    override fun playUrl(url: String, id: String) {
        player?.run {
            if (currentMediaItem?.mediaId != id) {
                coroutineScope.launch {
                    _playerUiState.value = _playerUiState.value.copy(
                        currentMediaItemId = id
                    )
                }

                if (isPlaying) stop()

                val mediaItem = MediaItem.Builder()
                    .setUri(url)
                    .setMediaId(id)
                    .build()

                setMediaItem(mediaItem)
            }

            if (bufferedPosition > 0) {
                if (contentPosition in 1 until duration) {
                    seekTo(contentPosition)
                } else {
                    seekTo(0)
                }
            } else {
                prepare()
            }
            playWhenReady = true
        }
    }

    override fun playFile(path: String, id: String) {
        player?.run {
            if (currentMediaItem?.mediaId != id) {
                coroutineScope.launch {
                    _playerUiState.value = _playerUiState.value.copy(
                        currentMediaItemId = id
                    )
                }

                if (isPlaying) stop()

                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.fromFile(File(path)))
                    .setMediaId(id)
                    .build()

                setMediaItem(mediaItem)
            }

            if (bufferedPosition > 0) {
                if (contentPosition in 1 until duration) {
                    seekTo(contentPosition)
                } else {
                    seekTo(0)
                }
            } else {
                prepare()
            }
            playWhenReady = true
        }
    }

    override fun seekTo(millis: Long) {
        player?.run {
            if (millis >= duration) {
                seekTo(duration - 2000)
            } else if (millis <= 0) {
                seekTo(0)
            } else {
                seekTo(millis)
            }
        }
    }

    override fun seekFor(millis: Long) {
        player?.run {
            if (currentPosition + millis >= duration) {
                seekTo(duration - 2000)
            } else if (currentPosition + millis <= 0) {
                seekTo(0)
            } else {
                seekTo(currentPosition + millis)
            }
        }
    }

    override fun pause() {
        player?.pause()
    }

    @UnstableApi
    override fun release() {
        playerNotificationManager?.setPlayer(null)
        mediaSessionCompat?.release()
        player?.release()
        player = null
        mediaSessionCompat = null
        playerNotificationManager = null
    }

    override fun onStart(owner: LifecycleOwner) {
        player?.addListener(playerListener)
    }

    override fun onPause(owner: LifecycleOwner) {
        player?.pause()
    }

    override fun onStop(owner: LifecycleOwner) {
        player?.removeListener(playerListener)
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            events.onPlaybackButtonChanged(::changePlaybackState)
        }

        override fun onPlayerError(error: PlaybackException) {
            // do nothing
        }
    }

    private inline fun Player.Events.onPlaybackButtonChanged(changePlaybackState: () -> Unit) {
        if (containsAny(
                Player.EVENT_PLAYBACK_STATE_CHANGED,
                Player.EVENT_PLAY_WHEN_READY_CHANGED
            )
        ) {
            changePlaybackState()
        }
    }

    private fun changePlaybackState() {
        coroutineScope.launch {
            _playerUiState.value = _playerUiState.value.copy(
                audioState = player.state,
                audioDuration = player?.duration.takeIf { it != C.TIME_UNSET } ?: 0
            )
        }
    }

    private val Player?.state: AudioState
        get() = if (this != null) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    actualState
                }

                Player.STATE_ENDED -> {
                    player?.seekTo(0)
                    player?.pause()

                    actualState = AudioState.PAUSE
                    actualState
                }

                else -> {
                    actualState = if (isPlaying) AudioState.PLAYING else AudioState.PAUSE
                    actualState
                }
            }
        } else {
            AudioState.PAUSE
        }
}