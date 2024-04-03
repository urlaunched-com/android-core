package com.urlaunched.android.design.ui.paging

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.PagingData
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow

private const val DEFAULT_PLACEHOLDER_ITEMS_NUM = 10

@Composable
fun <T : Any> PagingPullRefreshColumn(
    modifier: Modifier = Modifier,
    pagingDataFlow: Flow<PagingData<T>>,
    contentPadding: PaddingValues = PaddingValues(),
    state: LazyListState = rememberLazyListState(),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    placeholderItemsNum: Int = DEFAULT_PLACEHOLDER_ITEMS_NUM,
    refreshIndicatorBackgroundColor: Color = Color.White,
    refreshIndicatorContentColor: Color = Color.Black,
    defaultIndicatorTrackColor: Color = ProgressIndicatorDefaults.circularTrackColor,
    defaultIndicatorColor: Color = ProgressIndicatorDefaults.circularColor,
    showSnackbar: suspend (message: String) -> Unit,
    placeholderItem: @Composable LazyItemScope.(index: Int) -> Unit,
    item: @Composable LazyItemScope.(item: T) -> Unit,
    itemKey: ((item: T) -> Any)? = null,
    itemContentType: ((item: T) -> Any)? = null,
    onLoadingError: @Composable LazyItemScope.(pagingState: PagingState<T>) -> Unit = {},
    startItems: (LazyListScope.(pagingState: PagingState<T>) -> Unit)? = null,
    endItems: (LazyListScope.(pagingState: PagingState<T>) -> Unit)? = null,
    noItemsPlaceholder: @Composable LazyItemScope.() -> Unit = {},
    appendIndicator: (@Composable LazyItemScope.() -> Unit)? = {
        CircularProgressIndicator(
            color = defaultIndicatorColor,
            trackColor = defaultIndicatorTrackColor
        )
    }
) {
    PagingPullRefreshColumn(
        modifier = modifier,
        pagingDataFlow = pagingDataFlow,
        contentPadding = contentPadding,
        state = state,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        placeholderItemsNum = placeholderItemsNum,
        refreshIndicatorBackgroundColor = refreshIndicatorBackgroundColor,
        refreshIndicatorContentColor = refreshIndicatorContentColor,
        defaultIndicatorTrackColor = defaultIndicatorTrackColor,
        defaultIndicatorColor = defaultIndicatorColor,
        showSnackbar = showSnackbar,
        itemKey = itemKey,
        itemContentType = itemContentType,
        placeholderItem = placeholderItem,
        startItems = startItems,
        noItemsPlaceholder = noItemsPlaceholder,
        appendIndicator = appendIndicator,
        endItems = endItems,
        onLoadingError = onLoadingError,
        item = { _, _, itemModel ->
            item(itemModel)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Any> PagingPullRefreshColumn(
    modifier: Modifier = Modifier,
    pagingDataFlow: Flow<PagingData<T>>,
    contentPadding: PaddingValues = PaddingValues(),
    state: LazyListState = rememberLazyListState(),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    placeholderItemsNum: Int = DEFAULT_PLACEHOLDER_ITEMS_NUM,
    refreshIndicatorBackgroundColor: Color = Color.White,
    refreshIndicatorContentColor: Color = Color.Black,
    defaultIndicatorTrackColor: Color = ProgressIndicatorDefaults.circularTrackColor,
    defaultIndicatorColor: Color = ProgressIndicatorDefaults.circularColor,
    showSnackbar: suspend (message: String) -> Unit,
    placeholderItem: @Composable LazyItemScope.(index: Int) -> Unit,
    item: @Composable LazyItemScope.(index: Int, itemCount: Int, item: T) -> Unit,
    itemKey: ((item: T) -> Any)? = null,
    itemContentType: ((item: T) -> Any)? = null,
    endItems: (LazyListScope.(pagingState: PagingState<T>) -> Unit)? = null,
    startItems: (LazyListScope.(pagingState: PagingState<T>) -> Unit)? = null,
    noItemsPlaceholder: @Composable LazyItemScope.() -> Unit = {},
    onLoadingError: @Composable LazyItemScope.(pagingState: PagingState<T>) -> Unit = {},
    appendIndicator: (@Composable LazyItemScope.() -> Unit)? = {
        CircularProgressIndicator(
            color = defaultIndicatorColor,
            trackColor = defaultIndicatorTrackColor
        )
    }
) {
    PagingContainer(
        pagingDataFlow = pagingDataFlow,
        showSnackbar = showSnackbar
    ) { pagingState ->
        var isUserInitiatedRefresh by remember { mutableStateOf(false) }

        val isRefreshing = isUserInitiatedRefresh && pagingState.isLoading

        val pullRefreshState = rememberPullRefreshState(
            onRefresh = {
                isUserInitiatedRefresh = true
                pagingState.pagingItems.refresh()
            },
            refreshing = isRefreshing
        )

        LaunchedEffect(isUserInitiatedRefresh, pagingState.isLoading) {
            if (!pagingState.isLoading && isUserInitiatedRefresh) {
                isUserInitiatedRefresh = false
            }
        }

        Box(modifier = modifier) {
            LazyColumn(
                modifier = Modifier.pullRefresh(state = pullRefreshState),
                contentPadding = contentPadding,
                state = if (pagingState.isLoading) rememberLazyListState() else state,
                reverseLayout = reverseLayout,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled
            ) {
                startItems?.invoke(this, pagingState)

                when {
                    pagingState.isLoading -> {
                        items(placeholderItemsNum) { index ->
                            placeholderItem(index)
                        }
                    }

                    pagingState.isNoItems -> {
                        item {
                            noItemsPlaceholder()
                        }
                    }

                    pagingState.isLoadingError -> {
                        item {
                            onLoadingError(pagingState)
                        }
                    }

                    else -> {
                        items(
                            count = pagingState.pagingItems.itemCount,
                            key = itemKey?.let {
                                pagingState.pagingItems.itemKey(itemKey)
                            },
                            contentType = pagingState.pagingItems.itemContentType(itemContentType)
                        ) { index ->
                            pagingState.pagingItems[index]?.let { item ->
                                item(index, pagingState.pagingItems.itemCount, item)
                            } ?: run {
                                placeholderItem(index)
                            }
                        }

                        if (pagingState.isAppendLoading && appendIndicator != null) {
                            item {
                                appendIndicator()
                            }
                        }
                    }
                }

                endItems?.invoke(this, pagingState)
            }

            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                refreshing = isRefreshing,
                state = pullRefreshState,
                backgroundColor = refreshIndicatorBackgroundColor,
                contentColor = refreshIndicatorContentColor
            )
        }

        LaunchedEffect(
            state.canScrollForward,
            pagingState.isAppendError,
            state.isScrollInProgress
        ) {
            if (!state.canScrollForward && pagingState.isAppendError && state.isScrollInProgress) pagingState.pagingItems.retry()
        }
    }
}