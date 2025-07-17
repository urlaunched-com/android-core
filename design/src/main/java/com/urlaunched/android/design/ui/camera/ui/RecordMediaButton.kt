package com.urlaunched.android.design.ui.camera.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens

private const val recordMediaButtonRecordingCornersPercent = 20
private const val recordMediaButtonDefaultCornersPercent = 100
private const val recordLabel = "recordingTransition"
private const val backgroundColorAlpha = 0.6f
private const val progressStartAngle = 270f

@Composable
internal fun RecordMediaButton(
    modifier: Modifier = Modifier,
    isRecording: Boolean,
    progress: Float,
    progressBrush: Brush,
    recordingButtonColor: Color = Color.Black,
    defaultButtonColor: Color  = Color.Red,
    onClick: () -> Unit
) {
    val backgroundWhite: Color  = Color.White
    val backgroundWhiteAlpha: Color  = backgroundWhite.copy(alpha = backgroundColorAlpha)
    val transparentWhite: Color  = backgroundWhite.copy(alpha = 0f)
    val recordingTransition = updateTransition(isRecording, label = recordLabel)

    val cornerRadius by recordingTransition.animateInt { recordingState ->
        if (recordingState) recordMediaButtonRecordingCornersPercent
        else recordMediaButtonDefaultCornersPercent
    }

    val innerPadding by recordingTransition.animateDp { recordingState ->
        if (recordingState) Dimens.spacingBig
        else Dimens.spacingSmall
    }

    val buttonColor by recordingTransition.animateColor {
        if (it) recordingButtonColor else defaultButtonColor
    }

    val backgroundColor by recordingTransition.animateColor {
        if (it) backgroundWhiteAlpha else transparentWhite
    }

    val borderColor by recordingTransition.animateColor {
        if (it) transparentWhite else backgroundWhite
    }

    val coercedProgress = progress.coerceIn(0f, 1f)
    val stroke = with(LocalDensity.current) {
        Stroke(
            width = CameraRecorderDimens.recordMediaButtonProgressWidth.toPx(),
            cap = StrokeCap.Round
        )
    }

    Box(
        modifier = modifier
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    if (isRecording) {
                        val sweep = coercedProgress * 360f
                        drawCircularIndicator(progressStartAngle, sweep, progressBrush, stroke)
                    }
                }
            }
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .border(
                width = CameraRecorderDimens.recordMediaButtonBorderWidth,
                color = borderColor,
                shape = CircleShape
            )
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
    ) {
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    color = buttonColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
        )
    }
}


private fun DrawScope.drawCircularIndicator(startAngle: Float, sweep: Float, brush: Brush, stroke: Stroke) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        brush = brush,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

@Preview(showBackground = true, backgroundColor = 0xD8FFFFFF)
@Composable
private fun RecordMediaButtonPreview() {
    var isRecording by remember { mutableStateOf(false) }

    RecordMediaButton(
        modifier = Modifier.size(80.dp),
        isRecording = isRecording,
        progress = 1f,
        progressBrush = SolidColor(Color.Black),
        onClick = {
            isRecording = !isRecording
        }
    )
}