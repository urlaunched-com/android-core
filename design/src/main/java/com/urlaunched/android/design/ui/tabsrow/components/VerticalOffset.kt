package com.urlaunched.android.design.ui.tabsrow.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.urlaunched.android.design.extensions.toPx
import kotlin.math.max
import kotlin.math.min

@Composable
fun Modifier.verticalOffset(
    firstScrollState: LazyGridState,
    secondScrollState: LazyGridState,
    initialOffset: Dp,
    maxOffset: Dp
): Modifier {
    val maxOffsetPx = maxOffset.toPx()
    val initialOffsetPx = initialOffset.toPx()
    val backgroundOffset by remember {
        derivedStateOf {
            val scrollStateOffset = if (firstScrollState.firstVisibleItemIndex == 0) {
                max(-firstScrollState.firstVisibleItemScrollOffset + initialOffsetPx, -maxOffsetPx)
            } else {
                -maxOffsetPx
            }

            val scrollStateSOffset = if (secondScrollState.firstVisibleItemIndex == 0) {
                max(-secondScrollState.firstVisibleItemScrollOffset + initialOffsetPx, -maxOffsetPx)
            } else {
                -maxOffsetPx
            }

            min(scrollStateOffset, scrollStateSOffset)
        }
    }

    return offset { IntOffset(x = 0, y = backgroundOffset) }
}

@Composable
fun Modifier.verticalOffset(
    firstScrollState: LazyListState,
    secondScrollState: LazyListState,
    initialOffset: Dp,
    maxOffset: Dp
): Modifier {
    val maxOffsetPx = maxOffset.toPx()
    val initialOffsetPx = initialOffset.toPx()
    val backgroundOffset by remember {
        derivedStateOf {
            val scrollStateOffset = if (firstScrollState.firstVisibleItemIndex == 0) {
                max(-firstScrollState.firstVisibleItemScrollOffset + initialOffsetPx, -maxOffsetPx)
            } else {
                -maxOffsetPx
            }

            val scrollStateSOffset = if (secondScrollState.firstVisibleItemIndex == 0) {
                max(-secondScrollState.firstVisibleItemScrollOffset + initialOffsetPx, -maxOffsetPx)
            } else {
                -maxOffsetPx
            }

            min(scrollStateOffset, scrollStateSOffset)
        }
    }

    return offset { IntOffset(x = 0, y = backgroundOffset) }
}