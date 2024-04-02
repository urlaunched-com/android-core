package com.urlaunched.design.ui.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : Any> PagingContainer(
    pagingDataFlow: Flow<PagingData<T>>,
    showSnackbar: suspend (message: String) -> Unit,
    pagingContent: @Composable (pagingState: PagingState<T>) -> Unit
) {
    val pagingItems = pagingDataFlow.collectAsLazyPagingItems()

    val localPagingMode = LocalPagingMode.current

    val isLoadingError by remember(pagingItems) {
        derivedStateOf {
            if (localPagingMode == null) {
                pagingItems.loadState.refresh is LoadState.Error
            } else {
                localPagingMode == LocalPagingModeEnum.ERROR
            }
        }
    }

    val isLoading by remember(pagingItems) {
        derivedStateOf {
            if (localPagingMode == null) {
                pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemSnapshotList.isEmpty()
            } else {
                localPagingMode == LocalPagingModeEnum.LOADING
            }
        }
    }
    val isAppendLoading by remember(pagingItems) {
        derivedStateOf {
            if (localPagingMode == null) {
                pagingItems.loadState.append is LoadState.Loading
            } else {
                localPagingMode == LocalPagingModeEnum.LOADING
            }
        }
    }
    val isAppendError by remember(pagingItems) {
        derivedStateOf {
            if (localPagingMode == null) {
                pagingItems.loadState.append is LoadState.Error
            } else {
                localPagingMode == LocalPagingModeEnum.ERROR
            }
        }
    }
    val isNoItems by remember(pagingItems) {
        derivedStateOf {
            if (localPagingMode == null) {
                pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.loadState.append.endOfPaginationReached && pagingItems.itemSnapshotList.isEmpty()
            } else {
                localPagingMode == LocalPagingModeEnum.EMPTY
            }
        }
    }

    LaunchedEffect(isLoadingError) {
        if (isLoadingError) {
            showSnackbar((pagingItems.loadState.refresh as LoadState.Error).error.message.toString())
        }
    }

    LaunchedEffect(isAppendError) {
        if (isAppendError) {
            showSnackbar((pagingItems.loadState.append as LoadState.Error).error.message.toString())
        }
    }

    pagingContent(
        PagingState(
            pagingItems = pagingItems,
            isLoading = isLoading,
            isAppendError = isAppendError,
            isAppendLoading = isAppendLoading,
            isLoadingError = isLoadingError,
            isNoItems = isNoItems
        )
    )
}