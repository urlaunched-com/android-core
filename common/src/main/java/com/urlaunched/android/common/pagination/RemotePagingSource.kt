package com.urlaunched.android.common.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response

fun <T : Any> remotePagingSource(
    initialPage: Int? = null,
    refreshKey: (state: PagingState<Int, T>) -> Int? = { null },
    loadItems: suspend (page: Int, pageSize: Int) -> Response<List<T>>
) = RemotePagingSource(refreshKey = refreshKey, loadItems = loadItems, initialPage = initialPage)

class RemotePagingSource<T : Any> internal constructor(
    private val initialPage: Int? = null,
    private val refreshKey: (state: PagingState<Int, T>) -> Int? = { null },
    private val loadItems: suspend (page: Int, pageSize: Int) -> Response<List<T>>
) : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = refreshKey(state)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: initialPage ?: 1

        return handleResponse(
            response = {
                loadItems(page, params.loadSize)
            },
            success = { list ->
                val isEndOfPagination = list.size < params.loadSize

                if (list.isNotEmpty()) {
                    Firebase.crashlytics.log("page: $page ${list.map { it.toString() }}")
                }

                LoadResult.Page(
                    data = list,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isEndOfPagination) null else page + 1
                )
            },
            error = { error ->
                LoadResult.Error(Exception(error.message.toString()))
            }
        )
    }

    private suspend fun handleResponse(
        response: suspend () -> Response<List<T>>,
        success: suspend (data: List<T>) -> LoadResult<Int, T>,
        error: suspend (error: ErrorData) -> LoadResult<Int, T>
    ): LoadResult<Int, T> = when (val result = response()) {
        is Response.Success -> {
            success(result.data)
        }

        is Response.Error -> {
            error(result.error)
        }
    }
}