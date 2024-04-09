package com.urlaunched.android.player.mediacontroller

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import com.urlaunched.android.player.mediacontroller.models.MediaControllerEventType
import com.urlaunched.android.player.mediacontroller.models.MediaControllerUiState
import com.urlaunched.android.player.mediacontroller.models.PlaybackSpeed
import com.urlaunched.android.player.mediacontroller.models.Track
import com.urlaunched.android.player.models.AudioState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MediaControllerManager(
    private val context: Context,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val mainImmediateDispatcher: CoroutineDispatcher,
    private val mediaSessionServiceClass: Class<out MediaSessionService>
) {
    private var playerDeferred = CompletableDeferred<Player>()
    private val coroutineScope = CoroutineScope(coroutineDispatcher + SupervisorJob())
    private val currentPlaylist = MutableStateFlow<List<Track>>(listOf())

    private val _timer = MutableStateFlow<Duration?>(null)
    val timer: StateFlow<Duration?> = _timer

    private var timerJob: Job? = null

    fun setTimer(timeMillis: Duration) {
        _timer.value = timeMillis
        timerJob?.cancel()

        timerJob = coroutineScope.launch {
            while (_timer.value?.isPositive() == true) {
                _timer.value = _timer.value?.minus(1.seconds)
                delay(ONE_SECOND)
            }
            _timer.value = 0.milliseconds
            pause()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timer.value = null
    }

    val mediaControllerUiState = callbackFlow {
        val factory = MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, mediaSessionServiceClass))
        ).buildAsync()
        val localPlayer = withContext(coroutineDispatcher) { factory.get() }
        playerDeferred.complete(localPlayer)

        // Reset current playlist info if exoplayer's playlist is empty
        if (localPlayer.mediaItemCount == 0) {
            currentPlaylist.update { listOf() }
        }

        val playerListener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) || events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)) {
                    launch {
                        send(player.getAudioStateEvent())
                    }
                }

                if (events.contains(Player.EVENT_PLAYBACK_PARAMETERS_CHANGED)) {
                    launch {
                        send(player.getPlaybackSpeedEvent())
                    }
                }

                if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                    launch {
                        send(player.getCurrentMediaItemEvent())
                    }
                }

                if (events.contains(Player.EVENT_PLAYER_ERROR)) {
                    // On network connection player error switch to next track in case not all chapters are downloaded
                    if (player.playerError?.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED) {
                        player.seekToNextMediaItem()
                        player.prepare()
                        player.playWhenReady = true
                    }
                }
            }
        }

        localPlayer.addListener(playerListener)

        // Send initial state events in case player is currently active
        launch {
            send(localPlayer.getAudioStateEvent())
            send(localPlayer.getPlaybackSpeedEvent())
            send(localPlayer.getCurrentMediaItemEvent())
        }

        launch {
            while (isActive) {
                send(
                    MediaControllerEventType.PositionEvent(
                        currentItemDuration = localPlayer.getCurrentDuration().milliseconds,
                        currentAudioPosition = localPlayer.getCurrentAudioPosition().milliseconds
                    )
                )

                delay(PLAYBACK_POSITION_POLL_RATE_MS)
            }
        }

        awaitClose {
            playerDeferred = CompletableDeferred()
            localPlayer.removeListener(playerListener)
            MediaController.releaseFuture(factory)
        }
    }
        .flowOn(mainImmediateDispatcher) // Exoplayer instances must be accessed on main thread
        .scan(
            initial = MediaControllerUiState(
                audioState = AudioState.PAUSE,
                currentAudioId = null,
                currentAudioPosition = 0.milliseconds,
                currentItemDuration = 0.milliseconds,
                hasNextItem = false,
                hasPreviousItem = false,
                currentTrack = null,
                playbackSpeed = PlaybackSpeed.X_1,
                playlist = listOf()
            )
        ) { initial, event ->
            initial.copy(
                audioState = if (event is MediaControllerEventType.AudioStateEvent) event.audioState else initial.audioState,
                currentAudioPosition = if (event is MediaControllerEventType.PositionEvent) event.currentAudioPosition else initial.currentAudioPosition,
                currentItemDuration = if (event is MediaControllerEventType.PositionEvent) event.currentItemDuration else initial.currentItemDuration,
                currentAudioId = if (event is MediaControllerEventType.MediaTransitionEvent) event.currentAudioId else initial.currentAudioId,
                hasPreviousItem = if (event is MediaControllerEventType.MediaTransitionEvent) event.hasPreviousItem else initial.hasPreviousItem,
                hasNextItem = if (event is MediaControllerEventType.MediaTransitionEvent) event.hasNextItem else initial.hasNextItem,
                playbackSpeed = if (event is MediaControllerEventType.PlaybackSpeedEvent) event.playbackSpeed else initial.playbackSpeed
            )
        }
        .distinctUntilChanged()
        .combine(currentPlaylist) { mediaState, playlist ->
            mediaState.copy(
                currentTrack = playlist.firstOrNull { track -> mediaState.currentAudioId == track.id.toString() },
                playlist = playlist
            )
        }
        .shareIn(scope = coroutineScope, started = SharingStarted.WhileSubscribed(), replay = 1)

    private fun Player.getCurrentMediaItemEvent() = MediaControllerEventType.MediaTransitionEvent(
        currentAudioId = currentMediaItem?.mediaId,
        hasNextItem = hasNextMediaItem(),
        hasPreviousItem = hasPreviousMediaItem(),
        playlistPosition = currentMediaItemIndex
    )

    private fun Player.getPlaybackSpeedEvent() = MediaControllerEventType.PlaybackSpeedEvent(
        playbackSpeed = PlaybackSpeed.entries.firstOrNull {
            it.speed == playbackParameters.speed
        } ?: PlaybackSpeed.X_1
    )

    private fun Player.getAudioStateEvent() = MediaControllerEventType.AudioStateEvent(
        when {
            playbackState == Player.STATE_BUFFERING -> AudioState.BUFFERING
            playbackState == Player.STATE_ENDED -> AudioState.PAUSE
            isPlaying -> AudioState.PLAYING
            !isPlaying -> AudioState.PAUSE
            else -> AudioState.PAUSE
        }
    )

    suspend fun setPlaylist(tracks: List<Track>, autoplay: Boolean = true) {
        playerDeferred.await().let { player ->
            withContext(mainImmediateDispatcher) {
                player.setMediaItems(
                    tracks.map { track ->
                        MediaItem.Builder()
                            .setUri(track.url)
                            .setMediaId(track.id.toString())
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setMediaType(MediaMetadata.MEDIA_TYPE_AUDIO_BOOK_CHAPTER)
                                    .setTitle(track.name)
                                    .setWriter(track.author)
                                    .setArtist(track.author)
                                    .setArtworkUri(track.imageMedia?.toUriOrNull())
                                    .build()
                            )
                            .build()
                    },
                    true
                )

                currentPlaylist.update { tracks }
                player.prepare()
                player.playWhenReady = autoplay
            }
        }
    }

    suspend fun playItem(track: Track, autoplay: Boolean = true) {
        setPlaylist(tracks = listOf(track), autoplay = autoplay)
    }

    suspend fun play() {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().play()
        }
    }

    suspend fun pause() {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().pause()
        }
    }

    suspend fun seekTo(position: Float) {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().let { player ->
                if (player.isCurrentMediaItemSeekable) {
                    player.seekTo((player.getCurrentDuration() * position.coerceIn(0f, 1f)).toLong())
                }
            }
        }
    }

    suspend fun seekForward(duration: Duration) {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().let { player ->
                player.seekTo(player.getCurrentAudioPosition() + duration.inWholeMilliseconds)
            }
        }
    }

    suspend fun seekBack(duration: Duration) {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().let { player ->
                player.seekTo((player.getCurrentAudioPosition() - duration.inWholeMilliseconds).coerceAtLeast(0))
            }
        }
    }

    suspend fun setPlaybackSpeed(playbackSpeed: PlaybackSpeed) {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().setPlaybackSpeed(playbackSpeed.speed)
        }
    }

    suspend fun playNext() {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().seekToNextMediaItem()
        }
    }

    suspend fun playPrevious() {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().seekToPreviousMediaItem()
        }
    }

    suspend fun playItemInPlaylistWithId(id: Int, autoplay: Boolean = true, customPosition: Duration? = null) {
        withContext(mainImmediateDispatcher) {
            currentPlaylist.value.indexOfFirst { it.id == id }.takeIf { it != INDEX_NOT_FOUND }?.let { position ->
                playerDeferred.await().let { player ->
                    player.seekToDefaultPosition(position)
                    player.prepare()
                    player.playWhenReady = autoplay
                    customPosition?.let { player.seekTo(it.inWholeMilliseconds) }
                }
            }
        }
    }

    suspend fun playItemInPlaylistByIndex(index: Int, autoplay: Boolean = true) {
        withContext(mainImmediateDispatcher) {
            playerDeferred.await().let { player ->
                player.seekToDefaultPosition(index)
                player.prepare()
                player.playWhenReady = autoplay
            }
        }
    }

    private fun Player.getCurrentAudioPosition() =
        currentPosition.takeIf { it != C.TIME_UNSET && it != C.TIME_END_OF_SOURCE } ?: 0L

    private fun Player.getCurrentDuration(): Long =
        duration.takeIf { it != C.TIME_UNSET && it != C.TIME_END_OF_SOURCE } ?: 0L

    private fun String.toUriOrNull() = runCatching { this.toUri() }.getOrNull()

    companion object {
        private const val PLAYBACK_POSITION_POLL_RATE_MS = 200L
        private const val INDEX_NOT_FOUND = -1
        private const val ONE_SECOND = 1000L
    }
}