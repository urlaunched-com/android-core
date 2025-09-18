package com.urlaunched.android.design.ui.downloadingprogressdialog

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.progresstext.ProgressText
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun AnimatedDownloadingProgressBar(
    progress: Float,
    trackColor: Color,
    progressColor: Color,
    modifier: Modifier = Modifier,
    downloaded: Float = progress,
    total: Float = 1f,
    progressBarHeight: Dp = Dimens.spacingLarge,
    trackShape: Shape = CircleShape,
    progressShape: Shape = trackShape,
    animationSpec: AnimationSpec<Float> = tween(),
    progressText: (@Composable BoxScope.(Float) -> Unit)? = null,
    supportingText: (@Composable RowScope.(Float) -> Unit)? = null
) {
    AnimatedDownloadingProgressBar(
        progress = progress,
        trackBrush = SolidColor(trackColor),
        progressBrush = SolidColor(progressColor),
        modifier = modifier,
        downloaded = downloaded,
        total = total,
        progressBarHeight = progressBarHeight,
        trackShape = trackShape,
        progressShape = progressShape,
        animationSpec = animationSpec,
        progressText = progressText,
        supportingText = supportingText
    )
}

@Composable
fun AnimatedDownloadingProgressBar(
    progress: Float,
    trackBrush: Brush,
    progressBrush: Brush,
    modifier: Modifier = Modifier,
    downloaded: Float = progress,
    total: Float = 1f,
    progressBarHeight: Dp = Dimens.spacingLarge,
    trackShape: Shape = CircleShape,
    progressShape: Shape = trackShape,
    animationSpec: AnimationSpec<Float> = tween(),
    progressText: (@Composable BoxScope.(Float) -> Unit)? = null,
    supportingText: (@Composable RowScope.(Float) -> Unit)? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = animationSpec
    )
    val animatedDownloaded by animateFloatAsState(
        targetValue = downloaded,
        animationSpec = animationSpec
    )

    DownloadingProgressBar(
        progress = animatedProgress,
        trackBrush = trackBrush,
        progressBrush = progressBrush,
        modifier = modifier,
        downloaded = animatedDownloaded,
        total = total,
        progressBarHeight = progressBarHeight,
        trackShape = trackShape,
        progressShape = progressShape,
        progressText = progressText,
        supportingText = supportingText
    )
}

@Composable
fun DownloadingProgressBar(
    progress: Float,
    trackColor: Color,
    progressColor: Color,
    modifier: Modifier = Modifier,
    downloaded: Float = progress,
    total: Float = 1f,
    progressBarHeight: Dp = Dimens.spacingLarge,
    trackShape: Shape = CircleShape,
    progressShape: Shape = trackShape,
    progressText: (@Composable BoxScope.(Float) -> Unit)? = null,
    supportingText: (@Composable RowScope.(Float) -> Unit)? = null
) {
    DownloadingProgressBar(
        progress = progress,
        trackBrush = SolidColor(trackColor),
        progressBrush = SolidColor(progressColor),
        modifier = modifier,
        downloaded = downloaded,
        total = total,
        progressBarHeight = progressBarHeight,
        trackShape = trackShape,
        progressShape = progressShape,
        progressText = progressText,
        supportingText = supportingText
    )
}

@Composable
fun DownloadingProgressBar(
    progress: Float,
    trackBrush: Brush,
    progressBrush: Brush,
    modifier: Modifier = Modifier,
    downloaded: Float = progress,
    total: Float = 1f,
    progressBarHeight: Dp = Dimens.spacingLarge,
    trackShape: Shape = CircleShape,
    progressShape: Shape = trackShape,
    progressText: (@Composable BoxScope.(Float) -> Unit)? = null,
    supportingText: (@Composable RowScope.(Float) -> Unit)? = null
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    Column(modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            ProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(progressBarHeight),
                progress = safeProgress,
                trackBrush = trackBrush,
                progressBrush = progressBrush,
                trackShape = trackShape,
                progressShape = progressShape
            )

            progressText?.invoke(this@Box, safeProgress)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            supportingText?.invoke(this@Row, downloaded)
            supportingText?.invoke(this@Row, total)
        }
    }
}

@Composable
private fun ProgressBar(
    progress: Float,
    modifier: Modifier,
    trackBrush: Brush,
    progressBrush: Brush,
    trackShape: Shape,
    progressShape: Shape
) {
    Canvas(modifier = modifier) {
        val trackOutline = trackShape.createOutline(size, layoutDirection, this)
        drawOutline(
            brush = trackBrush,
            outline = trackOutline
        )

        val progressWidth = size.width * progress
        val progressSize = size.copy(width = progressWidth)
        val progressOutline = progressShape.createOutline(progressSize, layoutDirection, this)

        clipPath(
            path = Path().apply { addOutline(trackOutline) }
        ) {
            drawOutline(
                brush = progressBrush,
                outline = progressOutline
            )
        }
    }
}

@Preview
@Composable
private fun GradientProgressBarPreview() {
    val progress = remember { 0.6f }
    DownloadingProgressBar(
        progress = progress,
        progressBarHeight = 50.dp,
        progressBrush = Brush.horizontalGradient(
            0f to Color.Cyan,
            progress to Color.Magenta
        ),
        trackBrush = SolidColor(Color.LightGray),
        supportingText = { value ->
            Text(
                text = "$value MB",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    horizontal = Dimens.spacingSmall,
                    vertical = Dimens.spacingTinyHalf
                )
            )
        }
    )
}

@Preview
@Composable
private fun SolidProgressBarPreview() {
    DownloadingProgressBar(
        progress = 0.75f,
        progressBarHeight = 50.dp,
        progressColor = Color.Yellow,
        trackColor = Color.LightGray
    )
}

@Preview
@Composable
private fun AnimatedProgressBarPreview() {
    var progress by remember { mutableFloatStateOf(0.33f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000)
            if (progress < 1f) {
                progress += 0.1f
            } else {
                progress = 0f
            }
        }
    }

    AnimatedDownloadingProgressBar(
        progress = progress,
        progressBarHeight = 50.dp,
        progressColor = Color.Red,
        trackColor = Color.LightGray,
        progressText = { animatedProgress ->
            ProgressText(
                text = "${"%.1f".format(animatedProgress * 100f)} %",
                progress = animatedProgress,
                startColor = Color.White,
                endColor = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        },
        supportingText = { value ->
            Text(
                text = "${"%.2f".format(value)} MB",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    horizontal = Dimens.spacingSmall,
                    vertical = Dimens.spacingTinyHalf
                )
            )
        }
    )
}