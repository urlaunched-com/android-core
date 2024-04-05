package com.urlaunched.android.player.signleplayerstate

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.urlaunched.android.player.signleplayerstate.model.PlayerUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface SinglePlayerState : DefaultLifecycleObserver {
    val playerUiState: StateFlow<PlayerUiState>
    val currentPlayingPosition: Long
    val player: Player?
    fun playUrl(url: String, id: String)
    fun playFile(path: String, id: String)
    fun pause()
    fun release()
    fun seekTo(millis: Long)
    fun seekFor(millis: Long)
}

@Composable
@UnstableApi
fun rememberPlayerState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    notificationData: NotificationPlayerHelper.NotificationData? = null,
    notificationChannelName: String?
): SinglePlayerState = if (LocalInspectionMode.current) {
    remember {
        NoOpSinglePlayerStateImpl()
    }
} else {
    remember(context, coroutineScope, notificationData) {
        SinglePlayerStateImpl(context, coroutineScope, notificationData, notificationChannelName)
    }
}