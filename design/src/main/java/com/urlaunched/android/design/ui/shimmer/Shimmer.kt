package com.urlaunched.android.design.ui.shimmer

import androidx.annotation.FloatRange
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.eygraber.compose.placeholder.PlaceholderDefaults
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.placeholder
import com.urlaunched.android.design.resources.dimens.Dimens
import kotlin.math.max

fun Modifier.shimmer(
    backgroundColor: Color = Color(0xFFDADADA),
    highlightColor: Color = Color(0xFFFFFFFF),
    shape: Shape = RoundedCornerShape(Dimens.cornerRadiusLarge)
) = then(
    placeholder(
        visible = true,
        color = backgroundColor,
        highlight = PlaceholderHighlight.linearGradientShimmer(highlightColor),
        shape = shape
    )
)

private fun PlaceholderHighlight.Companion.linearGradientShimmer(
    highlightColor: Color,
    animationSpec: InfiniteRepeatableSpec<Float> = PlaceholderDefaults.shimmerAnimationSpec,
    @FloatRange(from = 0.0, to = 1.0) progressForMaxAlpha: Float = 0.6f
): PlaceholderHighlight = Shimmer(
    highlightColor = highlightColor,
    animationSpec = animationSpec,
    progressForMaxAlpha = progressForMaxAlpha
)

private data class Shimmer(
    private val highlightColor: Color,
    override val animationSpec: InfiniteRepeatableSpec<Float>,
    private val progressForMaxAlpha: Float = 0.6f
) : PlaceholderHighlight {
    override fun brush(progress: Float, size: Size): Brush {
        val max = max(size.width, size.height)
        val delta = 2 * max * progress
        return Brush.linearGradient(
            0.1f to highlightColor.copy(alpha = 0f),
            0.5f to highlightColor,
            0.9f to highlightColor.copy(alpha = 0f),
            start = Offset(x = -max + delta, y = 0f + delta),
            end = Offset(x = 0f + delta, y = 0f + delta)
        )
    }

    override fun alpha(progress: Float): Float = progressForMaxAlpha
}