package com.urlaunched.android.design.ui.player.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.player.models.AudioSliderDimens
import com.urlaunched.android.design.ui.player.models.AudioSliderColor
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AudioSlider(
    modifier: Modifier = Modifier,
    audioProgress: Float,
    audioSliderDimens: AudioSliderDimens = AudioSliderDimens(),
    audioSliderColor: AudioSliderColor = AudioSliderColor(),
    audioSliderOnValueChange: (Float) -> Unit,
    thumb: @Composable () -> Unit,
    currentPositionText: @Composable () -> Unit,
    timeLeftText: @Composable () -> Unit
) {
    Slider(
        modifier = modifier,
        value = audioProgress,
        onValueChange = audioSliderOnValueChange,
        colors = SliderDefaults.colors(
            thumbColor = audioSliderColor.thumbColor,
            activeTrackColor = audioSliderColor.activeTrackColor,
            inactiveTrackColor = audioSliderColor.inactiveTrackColor
        ),
        thumb = {
            thumb()
        },
        track = { sliderState ->
            SliderDefaults.Track(
                modifier = Modifier.height(audioSliderDimens.sliderTrackHeight),
                colors = SliderDefaults.colors(
                    thumbColor = audioSliderColor.thumbColor,
                    activeTrackColor = audioSliderColor.activeTrackColor,
                    inactiveTrackColor = audioSliderColor.inactiveTrackColor
                ),
                enabled = true,
                sliderState = sliderState,
                thumbTrackGapSize = audioSliderDimens.sliderThumbTrackGapSize,
                trackInsideCornerSize = audioSliderDimens.sliderTrackInsideCornerSize,
                drawStopIndicator = null
            )
        }
    )

    Row(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = -Dimens.spacingNormalSpecial
                        .toPx()
                        .roundToInt()
                )
            }
            .padding(horizontal = Dimens.spacingNormal)
    ) {
        currentPositionText()

        Spacer(modifier = Modifier.weight(1f))

        timeLeftText()
    }
}