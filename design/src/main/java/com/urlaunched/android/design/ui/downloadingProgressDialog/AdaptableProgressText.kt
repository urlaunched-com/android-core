package com.urlaunched.android.design.ui.downloadingProgressDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AdaptableProgressText(
    text: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    Text(
        text = text,
        style = style,
        color = Color.White,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        modifier = modifier.blendMode(
            blendMode = BlendMode.Exclusion
        )
    )
}

private fun Modifier.blendMode(blendMode: BlendMode): Modifier = this.drawWithCache {
    val graphicsLayer = obtainGraphicsLayer()
    graphicsLayer.apply {
        record {
            drawContent()
        }
        this.blendMode = blendMode
    }
    onDrawWithContent {
        drawLayer(graphicsLayer)
    }
}

@Preview
@Composable
private fun AdaptableProgressTextPreview() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Row {
            Box(
                Modifier
                    .size(100.dp, 100.dp)
                    .background(Color.Yellow)
            )
            Box(
                Modifier
                    .size(100.dp, 100.dp)
                    .background(Color.Blue)
            )
        }
        AdaptableProgressText(
            text = "ADOPTED TEXT TEST"
        )
    }
}