package com.urlaunched.android.design.ui.player.models

import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens

data class AudioSliderDimens(
    val sliderThumbTrackGapSize: Dp = Dimens.zeroDp,
    val sliderTrackInsideCornerSize: Dp = Dimens.zeroDp,
    val sliderTrackHeight: Dp = Dimens.spacingTiny,
    val sliderTrackHorizontalPadding: Dp = Dimens.spacingSmall
)