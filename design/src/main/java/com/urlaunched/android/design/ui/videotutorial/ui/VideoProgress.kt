package com.urlaunched.android.design.ui.videotutorial.ui

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.modifiers.ifNotNull
import com.urlaunched.android.design.ui.shadow.models.ShadowStyle
import com.urlaunched.android.design.ui.shadow.shadow
import com.urlaunched.android.design.ui.videotutorial.constants.VideoProgressDefaults

@Composable
internal fun VideoProgress(
    modifier: Modifier = Modifier,
    mediaCount: Int,
    currentMediaIndex: Int,
    @FloatRange(0.0, 1.0)
    currentMediaProgress: Float,
    trackColor: Color = Color.LightGray,
    progressColor: Color = Color.White,
    shadow: ShadowStyle? = VideoProgressDefaults.DefaultProgressShadow,
    gapSize: Dp = Dimens.spacingTiny,
    closeButton: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier.statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gapSize)
    ) {
        repeat(mediaCount) { index ->
            LinearProgressIndicator(
                modifier = Modifier
                    .ifNotNull(shadow) { Modifier.shadow(it) }
                    .weight(1f),
                strokeCap = StrokeCap.Round,
                trackColor = trackColor,
                color = progressColor,
                gapSize = Dimens.zeroDp,
                drawStopIndicator = {
                    // Do nothing
                },
                progress = {
                    when {
                        currentMediaIndex > index -> 1f
                        currentMediaIndex == index -> currentMediaProgress
                        else -> 0f
                    }
                }
            )
        }

        closeButton()
    }
}

@Preview
@Composable
private fun VideoProgressPreview() {
    VideoProgress(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spacingNormal),
        progressColor = Color.Red,
        mediaCount = 5,
        currentMediaIndex = 3,
        currentMediaProgress = 0.5f
    )
}