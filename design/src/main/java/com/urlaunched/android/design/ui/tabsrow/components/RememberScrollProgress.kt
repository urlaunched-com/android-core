@file:Suppress("ktlint:standard:filename")

package com.urlaunched.android.design.ui.tabsrow.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.extensions.toPx
import kotlin.math.min

@Composable
fun LazyGridState.rememberScrollProgress(targetOffset: Dp): State<Float> {
    val firstItemOffsetPx = targetOffset.toPx()
    return remember {
        derivedStateOf {
            if (firstVisibleItemIndex == 0) {
                min(1f / firstItemOffsetPx * firstVisibleItemScrollOffset, 1f)
            } else {
                1f
            }
        }
    }
}

@Composable
fun LazyListState.rememberScrollProgress(targetOffset: Dp): State<Float> {
    val firstItemOffsetPx = targetOffset.toPx()
    return remember {
        derivedStateOf {
            if (firstVisibleItemIndex == 0) {
                min(1f / firstItemOffsetPx * firstVisibleItemScrollOffset, 1f)
            } else {
                1f
            }
        }
    }
}