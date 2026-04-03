package com.urlaunched.android.design.ui.videotutorial.constants

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.shadow.models.ShadowStyle

internal object VideoProgressDefaults {

    private const val SHADOW_ALPHA = 0.15f

    private val shadowBlurRadius = 4.dp
    private val shadowOffset = DpOffset(0.dp, 2.dp)

    val DefaultProgressShadow = ShadowStyle(
        color = Color.Black,
        alpha = SHADOW_ALPHA,
        cornersRadius = Dimens.cornerRadiusLarge,
        blurRadius = shadowBlurRadius,
        offset = shadowOffset
    )
}