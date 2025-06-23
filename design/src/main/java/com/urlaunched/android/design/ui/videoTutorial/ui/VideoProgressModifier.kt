package com.urlaunched.android.design.ui.videoTutorial.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.ui.shadow.shadow
import com.urlaunched.android.design.ui.videoTutorial.utils.ShadowConstants
import com.urlaunched.android.design.ui.videoTutorial.utils.ShadowDimens

@Composable
fun Modifier.dropShadow(
    color: Color = Color.Black,
    alpha: Float = ShadowConstants.DROP_SHADOW_ALPHA,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = ShadowDimens.dropShadowBlurRadius,
    offset: DpOffset = ShadowDimens.dropShadowOffset
) = this.shadow(
    alpha = alpha,
    shadowBlurRadius = shadowBlurRadius,
    offset = offset,
    color = color,
    cornersRadius = cornersRadius
)