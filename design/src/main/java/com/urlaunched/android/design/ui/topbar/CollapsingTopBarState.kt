package com.urlaunched.android.design.ui.topbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.extensions.toPx

class CollapsingTopBarState(
    val minHeightPx: Float,
    val maxHeightPx: Float,
    private val density: Density,
    strategy: CollapsingStrategy
) {
    var heightPx by mutableFloatStateOf(maxHeightPx)

    val heightDp by derivedStateOf { with(density) { heightPx.toDp() } }

    val fraction by derivedStateOf { 1f / (maxHeightPx - minHeightPx) * (heightPx - minHeightPx) }

    val collapseFraction by derivedStateOf { 1f - fraction }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (heightPx > maxHeightPx) return Offset.Zero

            if (available.y > 0f) {
                when {
                    strategy == CollapsingStrategy.ShowAtTheEnd -> return Offset.Zero
                    strategy == CollapsingStrategy.ShowOnScroll && heightPx >= maxHeightPx -> return Offset.Zero
                }
            }

            val oldHeight = heightPx
            val newHeight = (heightPx + available.y).coerceIn(minHeightPx, maxHeightPx)
            heightPx = newHeight

            return Offset(0f, newHeight - oldHeight)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (available.y > 0) {
                val oldHeight = heightPx
                val newHeight = (heightPx + available.y).coerceIn(minHeightPx, maxHeightPx)
                heightPx = newHeight

                return Offset(0f, (heightPx - oldHeight))
            }

            return Offset.Zero
        }
    }
}

@Composable
fun rememberCollapsingTopBarState(
    minHeight: Dp,
    maxHeight: Dp,
    collapsingStrategy: CollapsingStrategy = CollapsingStrategy.ShowAtTheEnd
): CollapsingTopBarState {
    val density = LocalDensity.current
    val minHeightPx = minHeight.toPx().toFloat()
    val maxHeightPx = maxHeight.toPx().toFloat()

    return remember(minHeight, maxHeight) {
        CollapsingTopBarState(
            minHeightPx = minHeightPx,
            maxHeightPx = maxHeightPx,
            density = density,
            strategy = collapsingStrategy
        )
    }
}