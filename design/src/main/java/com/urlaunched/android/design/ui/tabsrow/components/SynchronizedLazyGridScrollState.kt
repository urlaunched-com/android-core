package com.urlaunched.android.design.ui.tabsrow.components

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import com.urlaunched.android.design.extensions.toPx
import com.urlaunched.android.design.resources.dimens.Dimens
import kotlin.math.min

data class SynchronizedLazyGridScrollState(
    val firstTabState: LazyGridState,
    val secondTabState: LazyGridState,
    val targetOffset: Int,
    private val firstItemOffsetPx: Int
) {
    val firstTabStateProgress by firstTabState.getProgressState()
    val secondTabStateProgress by secondTabState.getProgressState()

    internal suspend fun syncFirstTab() {
        with(firstTabState) {
            if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset <= targetOffset) {
                scrollToItem(0, (targetOffset * secondTabStateProgress).toInt())
            }
        }
    }

    internal suspend fun syncSecondTab() {
        with(secondTabState) {
            if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset <= targetOffset) {
                scrollToItem(0, (targetOffset * firstTabStateProgress).toInt())
            }
        }
    }

    suspend fun scrollToTopFirstTab(animated: Boolean = true) {
        with(firstTabState) {
            if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset > targetOffset || firstVisibleItemIndex > 0) {
                if (animated) {
                    animateScrollToItem(0, (targetOffset * secondTabStateProgress).toInt())
                } else {
                    scrollToItem(0, (targetOffset * secondTabStateProgress).toInt())
                }
            }
        }
    }

    suspend fun scrollToTopSecondTab(animated: Boolean = true) {
        with(secondTabState) {
            if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset > targetOffset || firstVisibleItemIndex > 0) {
                if (animated) {
                    animateScrollToItem(0, (targetOffset * secondTabStateProgress).toInt())
                } else {
                    scrollToItem(0, (targetOffset * firstTabStateProgress).toInt())
                }
            }
        }
    }

    private fun LazyGridState.getProgressState() = derivedStateOf {
        if (firstVisibleItemIndex == 0) {
            min(1f / firstItemOffsetPx * firstVisibleItemScrollOffset, 1f)
        } else {
            1f
        }
    }
}

@Composable
fun rememberSynchronizedLazyGridScrollStates(
    spacing: Dp = Dimens.spacingNormalSpecial,
    initialOffset: Dp = Dimens.spacingNormal
): SynchronizedLazyGridScrollState {
    val firstTabState = rememberLazyGridState()
    val secondTabState = rememberLazyGridState()

    val targetOffset = initialOffset.toPx()
    val firstItemOffsetPx = spacing.toPx()

    val synchronizedLazyGridScrollState = remember(spacing, initialOffset) {
        SynchronizedLazyGridScrollState(
            firstTabState = firstTabState,
            secondTabState = secondTabState,
            firstItemOffsetPx = firstItemOffsetPx,
            targetOffset = targetOffset
        )
    }

    LaunchedEffect(synchronizedLazyGridScrollState.secondTabStateProgress) {
        synchronizedLazyGridScrollState.syncFirstTab()
    }

    LaunchedEffect(synchronizedLazyGridScrollState.firstTabStateProgress) {
        synchronizedLazyGridScrollState.syncSecondTab()
    }

    return synchronizedLazyGridScrollState
}