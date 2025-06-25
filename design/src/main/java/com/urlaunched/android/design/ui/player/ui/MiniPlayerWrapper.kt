package com.urlaunched.android.design.ui.player.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.player.models.AudioPlayButtonColor
import com.urlaunched.android.design.ui.player.models.AudioPlayButtonDimens
import com.urlaunched.android.design.ui.player.models.AudioSliderColor
import com.urlaunched.android.design.ui.player.models.AudioSliderDimens
import com.urlaunched.android.design.ui.player.models.MiniPlayerColors
import com.urlaunched.android.design.ui.player.models.MiniPlayerConstants
import com.urlaunched.android.design.ui.player.models.MiniPlayerDimens
import com.urlaunched.android.design.ui.shimmer.shimmer

@Composable
fun MiniPlayerWrapper(
    // 🔷 Основні параметри
    modifier: Modifier = Modifier,
    currentChapterName: String?,
    bookInfoText: String?,
    isRtlEnabled: Boolean = false,
    audioState: Any,

    // 🔤 Стилі
    chapterTextStyle: TextStyle,
    bookInfoTextStyle: TextStyle,

    // 🎨 Кольори, розміри, константи
    miniPlayerColors: MiniPlayerColors = MiniPlayerColors(),
    miniPlayerConstants: MiniPlayerConstants = MiniPlayerConstants(),
    miniPlayerDimens: MiniPlayerDimens = MiniPlayerDimens(),

    // ▶️ Управління аудіо
    onPlayAudioClick: () -> Unit,
    onPlayerClick: () -> Unit,
    onSizeChange: (IntSize) -> Unit,

    // 🔘 Кнопки
    playingButton: @Composable () -> Unit,
    pauseButton: @Composable () -> Unit,
    rotateMinusButton: @Composable () -> Unit,
    leadingContent: @Composable () -> Unit,

    // 🎚️ Прогрес і слайдер
    audioProgress: Float,
    audioSliderOnValueChange: (Float) -> Unit,
    audioSliderColor: AudioSliderColor = AudioSliderColor(),
    audioSliderDimens: AudioSliderDimens = AudioSliderDimens(),

    // ⏺️ Кнопка відтворення: стилі і розміри
    audioPlayButtonColor: AudioPlayButtonColor = AudioPlayButtonColor(),
    audioPlayButtonDimens: AudioPlayButtonDimens = AudioPlayButtonDimens(),
    thumb: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides if (isRtlEnabled) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        leadingContent()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onPlayerClick, indication = null, interactionSource = null)
        ) {
            Spacer(Modifier.height(miniPlayerDimens.sliderHalfHeightOffset))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged(onSizeChange)
                    .background(color = miniPlayerColors.background)
            ) {
                Spacer(Modifier.height(Dimens.spacingNormalSpecial))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = miniPlayerColors.background)
                        .padding(end = Dimens.spacingNormal, start = Dimens.spacingExtraLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val textAnimation = fadeIn(
                        animationSpec = tween(
                            durationMillis = miniPlayerConstants.defaultFadeInDuration,
                            delayMillis = miniPlayerConstants.defaultFadeInDelay
                        )
                    ).togetherWith(fadeOut(animationSpec = tween(miniPlayerConstants.defaultFadeOutDuration)))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmallSpecial)
                    ) {
                        AnimatedContent(
                            targetState = currentChapterName,
                            transitionSpec = { textAnimation },
                            label = miniPlayerConstants.chapterNameAnimationLabel
                        ) { chapterName ->
                            if (chapterName != null) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = chapterName,
                                    style = chapterTextStyle,
                                    color = miniPlayerColors.textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shimmer(),
                                    text = miniPlayerConstants.emptyString,
                                    style = chapterTextStyle
                                )
                            }
                        }

                        AnimatedContent(
                            targetState = bookInfoText,
                            transitionSpec = { textAnimation },
                            label = miniPlayerConstants.chapterNameAnimationLabel
                        ) { bookName ->
                            if (bookName != null) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = bookName,
                                    style = bookInfoTextStyle,
                                    color = miniPlayerColors.textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .shimmer(),
                                    text = miniPlayerConstants.emptyString,
                                    style = bookInfoTextStyle
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(Dimens.spacingSmall))

                    rotateMinusButton()

                    Spacer(modifier = Modifier.width(Dimens.spacingSmall))

                    AudioPlayButton(
                        audioPlayButtonDimens = audioPlayButtonDimens,
                        audioState = audioState,
                        onClick = onPlayAudioClick,
                        playingButton = playingButton,
                        pauseButton = pauseButton,
                        audioPlayButtonColor = audioPlayButtonColor
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.spacingNormal))
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .background(miniPlayerColors.leadingLineColor)
                        .height(Dimens.spacingTiny)
                        .width(Dimens.spacingNormal)
                )

                Box(
                    Modifier
                        .background(miniPlayerColors.trailingLineColor)
                        .height(Dimens.spacingTiny)
                        .weight(1f)
                )
            }

            AudioSlider(
                modifier = Modifier.height(miniPlayerDimens.audioSliderHeight),
                audioProgress = audioProgress,
                audioSliderDimens = audioSliderDimens,
                audioSliderColor = audioSliderColor,
                audioSliderOnValueChange = audioSliderOnValueChange,
                thumb = thumb,
                currentPositionText = {},
                timeLeftText = {}
            )

            Spacer(modifier = Modifier.height(Dimens.spacingNormal))
        }
    }
}