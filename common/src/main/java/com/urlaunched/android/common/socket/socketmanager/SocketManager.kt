package com.urlaunched.android.common.socket.socketmanager

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertHeaderItem
import androidx.paging.insertSeparators
import androidx.paging.map
import com.urlaunched.android.common.socket.ActionCableSocketEventMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class SocketManager<I : Any, T : Any>(
    private val pagingDataFlow: Flow<PagingData<I>>,
    private val mapper: (I) -> T,
    private val itemId: (T) -> Long,
    private val itemCreatedAt: (T) -> Instant?,
    private val scope: CoroutineScope,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val refreshChannel = Channel<Unit>()
    private var updatesSocketJob: Job? = null
    private val accumulatedUpdates = MutableStateFlow(mapOf<Long, T>())
    private val accumulatedInserts = MutableStateFlow(mapOf<Long, T>())
    private val accumulatedDeletes = MutableStateFlow(mapOf<Long, T>())

    fun getFlow(): Flow<PagingData<T>> = combine(
        // Channel for ability to refresh by lifecycle action
        refreshChannel
            .receiveAsFlow()
            .onStart { emit(Unit) }
            .flatMapLatest { pagingDataFlow }
            .onEach {
                // Reset local state on list reload
                accumulatedUpdates.update { emptyMap() }
                accumulatedInserts.update { emptyMap() }
            }
            .cachedIn(scope),
        accumulatedUpdates,
        accumulatedInserts,
        accumulatedDeletes
    ) { pagingData, chatsUpdates, chatInserts, chatDeletes ->
        if (chatsUpdates.isEmpty() && chatInserts.isEmpty()) {
            pagingData.map { mapper(it) }
        } else {
            val flow = pagingData.map { data ->
                mapper(data)
            }

            chatInserts.values
                .sortedBy { chat -> itemCreatedAt(chat) }
                .fold(flow) { acc, data ->
                    acc.insertHeaderItem(item = data)
                }
        }
            .filter { chatDeletes.contains(itemId(it)).not() }
            .let { data ->
                if (chatsUpdates.isNotEmpty()) {
                    data.map {
                        chatsUpdates[itemId(it)] ?: it
                    }
                } else {
                    data
                }
            }
    }

    fun getFlow(
        separators: SocketManager<I, T>.(PagingData<DataType.Data<T>>) -> PagingData<DataType>,
        createdAt: (T) -> Instant
    ): Flow<PagingData<DataType>> = getFlow().map {
        it.map { data ->
            DataType.Data(data = data, createdAt = createdAt(data))
        }.run {
            with(this@SocketManager) {
                separators(this@run)
            }
        }
    }

    fun <T> PagingData<DataType.Data<T>>.insertDateSeparators(createdAt: (DataType.Data<T>) -> Instant) =
        insertSeparators { before: DataType.Data<T>?, after: DataType.Data<T>? ->
            val dateBefore = before?.let(createdAt)
            val dateAfter = after?.let(createdAt)
            val localDateBefore = dateBefore?.atZone(ZoneId.systemDefault())
            val localDateAfter = dateAfter?.atZone(ZoneId.systemDefault())

            when {
                dateAfter == null && dateBefore != null -> {
                    DataType.DateSeparator(dateBefore)
                }

                dateBefore != null && localDateBefore?.dayOfYear != localDateAfter?.dayOfYear -> {
                    DataType.DateSeparator(dateBefore)
                }

                else -> null
            }
        }

    fun <T> PagingData<DataType>.insertTimeSeparators(
        isAuthor: (DataType.Data<T>) -> Boolean,
        maxDurationBetweenData: Duration? = null
    ) = insertSeparators { before: DataType?, after: DataType? ->
        val beforeData = (before as? DataType.Data<T>)
        val afterData = (after as? DataType.Data<T>)

        when {
            before == null && after is DataType.Data<*> -> {
                DataType.TimeSeparator(data = after.data)
            }

            before is DataType.DateSeparator && (after as? DataType.Data<*>) != null -> {
                DataType.TimeSeparator(data = after.data)
            }

            beforeData?.let(isAuthor) == false && afterData?.let(isAuthor) == true -> {
                DataType.TimeSeparator(data = after.data)
            }

            beforeData?.let(isAuthor) == true && afterData?.let(isAuthor) == false -> {
                DataType.TimeSeparator(data = after.data)
            }

            beforeData != null && afterData != null && maxDurationBetweenData != null && Duration.between(
                afterData.createdAt,
                beforeData.createdAt
            ) > maxDurationBetweenData -> {
                DataType.TimeSeparator(data = after.data)
            }

            else -> null
        }
    }

    fun handleSocketAction(id: Long, data: SocketEvent<T>) {
        when (data) {
            is SocketEvent.New -> accumulatedInserts.update { updates ->
                updates.plus(id to data.data)
            }

            is SocketEvent.Updated -> accumulatedUpdates.update { updates ->
                updates.plus(id to data.data)
            }

            is SocketEvent.Delete -> accumulatedDeletes.update { updates ->
                updates.plus(id to data.data)
            }
        }
    }

    fun mapHandleSocketAction(id: Long, data: SocketEvent<I>) {
        when (data) {
            is SocketEvent.New -> accumulatedInserts.update { updates ->
                updates.plus(id to mapper(data.data))
            }

            is SocketEvent.Updated -> accumulatedUpdates.update { updates ->
                updates.plus(id to mapper(data.data))
            }

            is SocketEvent.Delete -> accumulatedDeletes.update { updates ->
                updates.plus(id to mapper(data.data))
            }
        }
    }

    fun update(id: Long, data: T) {
        accumulatedUpdates.update { updates ->
            updates.plus(id to data)
        }
    }

    fun delete(id: Long, data: T) {
        accumulatedDeletes.update { updates ->
            updates.plus(id to data)
        }
    }

    fun cancelDelete(id: Long) {
        accumulatedDeletes.update { updates ->
            updates.minus(id)
        }
    }

    fun mapUpdate(id: Long, data: I) {
        accumulatedUpdates.update { updates ->
            updates.plus(id to mapper(data))
        }
    }

    fun <S> subscribeUpdatesFromRemote(
        flow: Flow<ActionCableSocketEventMessage<S>>,
        onEvent: (ActionCableSocketEventMessage<S>) -> Unit
    ) {
        updatesSocketJob?.cancel()
        updatesSocketJob = scope.launch(coroutineDispatcher) {
            flow.collectLatest { socketMessage ->
                when (socketMessage) {
                    is ActionCableSocketEventMessage.Message -> {
                        onEvent(socketMessage)
                    }

                    is ActionCableSocketEventMessage.SubscriptionRejected -> {
                        onEvent(socketMessage)
                    }

                    is ActionCableSocketEventMessage.Error -> {
                        // Quietly resubscribe on network error
                        subscribeUpdatesFromRemote(flow, onEvent)
                        onEvent(socketMessage)
                    }

                    else -> {
                        onEvent(socketMessage)
                    }
                }
            }
        }
    }

    fun refresh() {
        scope.launch(coroutineDispatcher) {
            refreshChannel.send(Unit)
        }
    }

    fun cancel() {
        updatesSocketJob?.cancel()
    }
}