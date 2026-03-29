package com.urlaunched.android.design.ui.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.ui.player.models.BottomItemColors
import com.urlaunched.android.design.ui.player.models.BottomBarItem
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

    // 📊 Стан / значення
    audioProgress: Float,
    audioState: Any,

    // 🔁 Колбеки
    audioSliderOnValueChange: (Float) -> Unit,
    onPlayAudioClick: () -> Unit,

    // 🎨 Кольори
    audioPlayButtonColor: AudioPlayButtonColor = AudioPlayButtonColor(),
    audioSliderColor: AudioSliderColor = AudioSliderColor(),
    playerWrapperBottomColor: PlayerWrapperBottomColor = PlayerWrapperBottomColor(),
    bottomItemColors: BottomItemColors = BottomItemColors(),

    // 📐 Розміри
    audioSliderDimens: AudioSliderDimens = AudioSliderDimens(),
    playerWrapperBottomDimens: PlayerWrapperBottomDimens = PlayerWrapperBottomDimens(),
    playerDimens: PlayerDimens = PlayerDimens(),

    // 🎨 Стилі
    bottomTextStyle: TextStyle,

    // 📦 Дані
    bottomBarItems: List<BottomBarItem>,

    nextTrackButton: @Composable () -> Unit,
    previousTrackButton: @Composable () -> Unit,
    rotatePlusButton: @Composable () -> Unit,
    rotateMinusButton: @Composable () -> Unit,

    playingButton: @Composable () -> Unit,
    pauseButton: @Composable () -> Unit,
    thumb: @Composable () -> Unit,
    currentPositionText: @Composable () -> Unit,
    timeLeftText: @Composable () -> Unit,

    // додатковий контент
    chapterDetails: @Composable () -> Unit,
    audioTrackDetails: @Composable () -> Unit
) {
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
                    modifier = Modifier.padding(horizontal = audioSliderDimens.sliderTrackHorizontalPadding),
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

            BottomPanel(
                bottomBarItems = bottomBarItems,
                bottomTextStyle = bottomTextStyle,
                bottomItemColors = bottomItemColors
            )
        }
    }
}