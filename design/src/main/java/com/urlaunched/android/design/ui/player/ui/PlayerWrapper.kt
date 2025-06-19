package com.urlaunched.android.design.ui.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.urlaunched.android.design.ui.player.models.AudioPlayButtonColor
import com.urlaunched.android.design.ui.player.models.AudioSliderColor
import com.urlaunched.android.design.ui.player.models.AudioSliderDimens
import com.urlaunched.android.design.ui.player.models.PlayerDimens
import com.urlaunched.android.design.ui.player.models.PlayerWrapperBottomColor
import com.urlaunched.android.design.ui.player.models.PlayerWrapperBottomDimens

@Composable
fun PlayerWrapper(
    // 🌐 Загальне оточення / структура
    modifier: Modifier = Modifier,
    isRtlEnabled: Boolean = false,

    // 🎨 Кольори
    audioPlayButtonColor: AudioPlayButtonColor = AudioPlayButtonColor(),
    audioSliderColor: AudioSliderColor = AudioSliderColor(),
    playerWrapperBottomColor: PlayerWrapperBottomColor = PlayerWrapperBottomColor(),

    // 📐 Розміри
    audioSliderDimens: AudioSliderDimens = AudioSliderDimens(),
    playerWrapperBottomDimens: PlayerWrapperBottomDimens = PlayerWrapperBottomDimens(),
    playerDimens: PlayerDimens = PlayerDimens(),

    // 📊 Стан / значення
    audioProgress: Float,
    audioState: Any,

    // 🔁 Колбеки (взаємодія)
    audioSliderOnValueChange: (Float) -> Unit,
    onPlayAudioClick: () -> Unit,

    // 🧩 Компоненти UI (Composable блоки)
    // — елементи управління треком
    nextTrackButton: @Composable () -> Unit,
    previousTrackButton: @Composable () -> Unit,
    rotatePlusButton: @Composable () -> Unit,
    rotateMinusButton: @Composable () -> Unit,

    // — кнопки відтворення
    playingButton: @Composable () -> Unit,
    pauseButton: @Composable () -> Unit,

    // — прогрес
    thumb: @Composable () -> Unit,
    currentPositionText: @Composable () -> Unit,
    timeLeftText: @Composable () -> Unit,

    // — додатковий контент
    chapterDetails: @Composable () -> Unit,
    bottomPanel: @Composable () -> Unit,
    audioTrackDetails: @Composable () -> Unit
){

    Column(modifier = Modifier.fillMaxSize()) {

        AudioPlayerTrackDetails(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            audioTrackDetails = audioTrackDetails
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = playerWrapperBottomColor.backGround,
                    shape = RoundedCornerShape(
                        topStart = playerWrapperBottomDimens.topStartShape,
                        topEnd = playerWrapperBottomDimens.topEndShape
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            chapterDetails()

            CompositionLocalProvider(LocalLayoutDirection provides if (isRtlEnabled) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                AudioSlider(
                    audioProgress = audioProgress,
                    audioSliderDimens = audioSliderDimens,
                    audioSliderColor = audioSliderColor,
                    audioSliderOnValueChange = audioSliderOnValueChange,
                    thumb = thumb,
                    currentPositionText = currentPositionText,
                    timeLeftText = timeLeftText
                )

                Player(
                    audioState = audioState,
                    playerDimens = playerDimens,
                    onPlayAudioClick = onPlayAudioClick,
                    nextTrackButton = nextTrackButton,
                    rotatePlusButton = rotatePlusButton,
                    previousTrackButton = previousTrackButton,
                    rotateMinusButton = rotateMinusButton,
                    playingButton = playingButton,
                    pauseButton = pauseButton,
                    audioPlayButtonColor = audioPlayButtonColor

                )
            }

            bottomPanel()
        }
    }
}

