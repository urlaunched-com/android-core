package com.urlaunched.android.player.signleplayerstate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import java.io.File

object NotificationPlayerHelper {
    data class NotificationData(
        val title: String,
        val description: String,
        val imageFile: File?
    )

    fun initializerMediaSessionCompat(context: Context): MediaSessionCompat {
        val mediaSessionCompat = MediaSessionCompat(context, MEDIA_SESSION_COMPAT_TAG)
        return mediaSessionCompat
    }

    @UnstableApi
    fun initializePlayerNotificationManager(
        context: Context,
        mediaSessionCompat: MediaSessionCompat,
        player: Player?,
        notificationData: NotificationData,
        notificationChannelName: String
    ): PlayerNotificationManager {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            notificationChannelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setSound(null, null)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(
                object : PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): CharSequence = notificationData.title

                    override fun getCurrentContentText(player: Player): CharSequence = notificationData.description

                    override fun createCurrentContentIntent(player: Player): PendingIntent? = null

                    override fun getCurrentLargeIcon(
                        player: Player,
                        callback: PlayerNotificationManager.BitmapCallback
                    ): Bitmap? = notificationData.imageFile?.let { imageFile ->
                        BitmapFactory.decodeFile(imageFile.absolutePath)
                    }
                }
            )
            .build()
            .apply {
                setPlayer(player)
                setMediaSessionToken(mediaSessionCompat.sessionToken)
                setUseNextAction(true)
                setUsePreviousAction(true)
                setUseStopAction(false)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setColorized(true)
                setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            }
    }

    private const val MEDIA_SESSION_COMPAT_TAG = "MediaSessionCompatTag"
    private const val NOTIFICATION_CHANNEL_ID = "NOW_PLAYING_CHANNEL_ID"
    private const val NOTIFICATION_ID = 245
}