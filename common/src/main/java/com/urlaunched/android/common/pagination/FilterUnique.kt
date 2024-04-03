package com.urlaunched.android.common.pagination

import androidx.paging.PagingData
import androidx.paging.filter

fun <T : Any> PagingData<T>.filterUnique(comparisonField: (item: T) -> Any): PagingData<T> {
    val alreadyIncludedIds = mutableSetOf<Any>()

    return this.filter { item ->
        alreadyIncludedIds.add(comparisonField(item))
    }
}