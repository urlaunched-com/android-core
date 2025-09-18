package com.urlaunched.android.design.ui.progresstext

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.ui.progresstext.models.ProgressDirection

@Composable
fun ProgressText(
    modifier: Modifier = Modifier,
    text: String,
    @FloatRange(0.0, 1.0)
    progress: Float,
    startColor: Color,
    endColor: Color,
    progressDirection: ProgressDirection = ProgressDirection.Horizontal,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        onTextLayout = onTextLayout,
        lineHeight = lineHeight,
        textAlign = textAlign,
        modifier = modifier,
        style = style.copy(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to startColor,
                    progress to startColor,
                    progress to endColor,
                    1f to endColor
                ),
                start = progressDirection.getStartOffset(LocalLayoutDirection.current),
                end = progressDirection.getEndOffset(LocalLayoutDirection.current)
            )
        )
    )
}

@Preview
@Composable
private fun ProgressTextPreview() {
    val progress = remember { 0.59f }

    Box(
        modifier = Modifier
            .size(200.dp, 50.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(Color.Yellow)
        )

        ProgressText(
            text = "PROGRESS TEXT",
            progress = progress,
            startColor = Color.Red,
            endColor = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }
}