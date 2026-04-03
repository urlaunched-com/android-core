package com.urlaunched.android.design.ui.videotutorial.model

import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.shadow.models.ShadowStyle
import com.urlaunched.android.design.ui.videotutorial.constants.VideoProgressDefaults

data class VideoProgressStyle(
    val shadow: ShadowStyle? = VideoProgressDefaults.DefaultProgressShadow,
    val gapSize: Dp = Dimens.spacingTiny
)