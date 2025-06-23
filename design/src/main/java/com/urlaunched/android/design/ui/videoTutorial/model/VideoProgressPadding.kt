package com.urlaunched.android.design.ui.videoTutorial.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens

data class VideoProgressPadding(
    val start: Dp = Dimens.spacingNormal,
    val top: Dp = Dimens.spacingNormalSpecial,
    val end: Dp = Dimens.spacingNormal,
    val bottom: Dp = 0.dp,
    val spaceBetweenProgress: Dp = Dimens.spacingTiny
)