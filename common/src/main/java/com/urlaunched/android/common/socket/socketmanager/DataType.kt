package com.urlaunched.android.common.socket.socketmanager

import androidx.compose.runtime.Composable
import java.time.Instant

sealed class DataType {
    data class DateSeparator(val time: Instant) : DataType()
    data class TimeSeparator<T>(val data: T) : DataType()
    data class Data<T>(val data: T, val createdAt: Instant) : DataType()

    fun <T, R> safeType(
        dateSeparator: (DateSeparator) -> R,
        timeSeparator: (TimeSeparator<T>) -> R,
        data: (Data<T>) -> R
    ) = when {
        this::class == Data::class -> data(this as Data<T>)
        this::class == DateSeparator::class -> dateSeparator(this as DateSeparator)
        this::class == TimeSeparator::class -> timeSeparator(this as TimeSeparator<T>)
        else -> throw IllegalStateException("Unknown type of DataType class")
    }

    @Suppress("ComposableNaming")
    @Composable
    fun <T> safeType(
        dateSeparator: @Composable (DateSeparator) -> Unit,
        timeSeparator: @Composable (TimeSeparator<T>) -> Unit,
        data: @Composable (Data<T>) -> Unit
    ) = when {
        this::class == Data::class -> data(this as Data<T>)
        this::class == DateSeparator::class -> dateSeparator(this as DateSeparator)
        this::class == TimeSeparator::class -> timeSeparator(this as TimeSeparator<T>)
        else -> throw IllegalStateException("Unknown type of DataType class")
    }
}