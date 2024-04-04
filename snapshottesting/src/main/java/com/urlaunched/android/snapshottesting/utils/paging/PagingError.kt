package com.urlaunched.android.snapshottesting.utils.paging

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf

fun <T : Any> pagingErrorFlow() = flowOf(
    PagingData.from<T>(
        data = listOf(),
        sourceLoadStates = LoadStates(
            refresh = LoadState.Error(Throwable()),
            prepend = LoadState.Error(Throwable()),
            append = LoadState.Error(Throwable())
        )
    )
)