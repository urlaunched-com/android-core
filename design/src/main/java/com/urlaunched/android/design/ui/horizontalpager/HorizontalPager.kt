package com.urlaunched.android.design.ui.horizontalpager

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.HorizontalPager as ComposePager

@Composable
fun HorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    currentPageIndex: Int,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSize: PageSize = PageSize.Fill,
    beyondBoundsPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pageSpacing: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: TargetedFlingBehavior = PagerDefaults.flingBehavior(state = pagerState),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null,
    pageNestedScrollConnection: NestedScrollConnection = PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal),
    content: @Composable (page: Int) -> Unit
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier) {
            content(currentPageIndex)
        }
    } else {
        ComposePager(
            modifier = modifier,
            verticalAlignment = verticalAlignment,
            state = pagerState,
            pageSpacing = pageSpacing,
            contentPadding = contentPadding,
            pageSize = pageSize,
            beyondViewportPageCount = beyondBoundsPageCount,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
            reverseLayout = reverseLayout,
            key = key,
            pageNestedScrollConnection = pageNestedScrollConnection
        ) { pageIndex ->
            content(pageIndex)
        }
    }
}