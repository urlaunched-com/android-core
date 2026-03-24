package com.urlaunched.synchronizer.core

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.urlaunched.android.synchonizer.model.Synchronizable
import com.urlaunched.synchronizer.core.ModelSynchronizer.cancelDeleteModel
import com.urlaunched.synchronizer.core.ModelSynchronizer.deletedModel
import com.urlaunched.synchronizer.core.ModelSynchronizer.updateModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object ModelSynchronizer {
    val updateModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val deletedModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val cancelDeleteModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    suspend inline fun <reified ACTUAL : Synchronizable<ID>, ID> synchronize(
        crossinline getList: () -> List<ACTUAL>,
        crossinline onUpdate: (updatedList: List<ACTUAL>) -> Unit
    ) {
        coroutineScope {
            launch {
                updateModel
                    .filterIsInstance<ACTUAL>()
                    .collectLatest { updatedData ->
                        val currentList = getList()

                        onUpdate(
                            currentList.map { currentItem ->
                                if (currentItem.id == updatedData.id) {
                                    updatedData
                                } else {
                                    currentItem
                                }
                            }
                        )
                    }
            }

            launch {
                val deletedItemsPositions = mutableMapOf<ID, Int>()

                launch {
                    deletedModel
                        .filterIsInstance<ACTUAL>()
                        .collectLatest { deletedModel ->
                            val currentList = getList()

                            currentList.indexOf(deletedModel).takeIf { it != -1 }?.let { index ->
                                deletedItemsPositions.put(deletedModel.id, index)

                                onUpdate(
                                    currentList.filter { item -> item.id != deletedModel.id }
                                )
                            }
                        }
                }

                launch {
                    cancelDeleteModel
                        .filterIsInstance<ACTUAL>()
                        .collectLatest { cancelDeleteModel ->
                            val currentList = getList()

                            deletedItemsPositions[cancelDeleteModel.id]?.let { index ->
                                deletedItemsPositions.remove(cancelDeleteModel.id)
                                onUpdate(
                                    currentList.toMutableList().apply { add(index, cancelDeleteModel) }
                                )
                            }
                        }
                }
            }
        }
    }

    suspend inline fun <reified ACTUAL : Synchronizable<ID>, reified RELATED : Synchronizable<ID>, ID> synchronizeRelated(
        crossinline getList: () -> List<ACTUAL>,
        crossinline map: (ACTUAL, RELATED) -> ACTUAL,
        crossinline onUpdate: (updatedList: List<ACTUAL>) -> Unit
    ) {
        updateModel
            .filterIsInstance<RELATED>()
            .collectLatest { updatedData ->
                val currentList = getList()

                onUpdate(
                    currentList.map { currentItem ->
                        if (currentItem.id == updatedData.id) {
                            map(currentItem, updatedData)
                        } else {
                            currentItem
                        }
                    }
                )
            }
    }

    suspend inline fun <reified ACTUAL : Synchronizable<ID>, ID> synchronize(
        id: ID,
        crossinline onUpdate: (ACTUAL) -> Unit
    ) {
        updateModel
            .filterIsInstance<ACTUAL>()
            .filter { it.id == id }
            .collectLatest { updatedItem ->
                onUpdate(updatedItem)
            }
    }

    inline fun <reified ACTUAL : Synchronizable<*>> getUpdatesFlow() = updateModel.filterIsInstance<ACTUAL>()

    suspend fun emitUpdate(value: Synchronizable<*>) {
        updateModel.emit(value)
    }

    suspend fun emitDelete(value: Synchronizable<*>) {
        deletedModel.emit(value)
    }

    suspend fun emitCancelDelete(value: Synchronizable<*>) {
        cancelDeleteModel.emit(value)
    }
}

inline fun <reified ACTUAL : Synchronizable<ID>, reified RELATED : Synchronizable<ID>, ID> Flow<PagingData<ACTUAL>>.synchronizeRelated(
    coroutineScope: CoroutineScope,
    crossinline mapRelatedToActual: (ACTUAL, RELATED) -> ACTUAL,
    crossinline mapActualToRelated: (ACTUAL) -> RELATED,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): Flow<PagingData<ACTUAL>> {
    val accumulatedUpdates = MutableStateFlow<Map<ID, RELATED>>(mapOf())
    val updatedData = mutableSetOf<ID>()

    coroutineScope.launch(coroutineDispatcher) {
        updateModel
            .filterIsInstance<RELATED>()
            .collectLatest { modelUpdate ->
                accumulatedUpdates.update { currentData ->
                    currentData + Pair(modelUpdate.id, modelUpdate)
                }
            }
    }

    return combine(
        this
            .onEach {
                updatedData.clear()
            }
            .map { pagingData ->
                pagingData.map { item ->
                    if (!updatedData.contains(item.id)) {
                        mapActualToRelated(item)?.let { updateModel.emit(it) }
                        updatedData.add(item.id)
                    }

                    item
                }
            }
            .cachedIn(coroutineScope),
        accumulatedUpdates
    ) { pagingData, updates ->
        pagingData.map { item ->
            updates[item.id]?.let { mapRelatedToActual(item, it) } ?: item
        }
    }.cachedIn(coroutineScope)
}

inline fun <reified MODEL : Synchronizable<ID>, ID> Flow<PagingData<MODEL>>.synchronize(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): Flow<PagingData<MODEL>> {
    val accumulatedUpdates = MutableStateFlow<Map<ID, MODEL>>(mapOf())
    val accumulatedDeletions = MutableStateFlow<Map<ID, MODEL>>(mapOf())
    val updatedData = mutableSetOf<ID>()

    coroutineScope.launch(coroutineDispatcher) {
        updateModel
            .filterIsInstance<MODEL>()
            .collectLatest { modelUpdate ->
                accumulatedUpdates.update { currentData ->
                    currentData + Pair(modelUpdate.id, modelUpdate)
                }
            }
    }

    coroutineScope.launch(coroutineDispatcher) {
        deletedModel
            .filterIsInstance<MODEL>()
            .collectLatest { modelDeletion ->
                accumulatedDeletions.update { currentData ->
                    currentData + Pair(modelDeletion.id, modelDeletion)
                }
            }
    }

    coroutineScope.launch(coroutineDispatcher) {
        cancelDeleteModel
            .filterIsInstance<MODEL>()
            .collectLatest { modelDeletionCancellation ->
                accumulatedDeletions.update { currentData ->
                    currentData - modelDeletionCancellation.id
                }
            }
    }

    return combine(
        this
            .onEach {
                updatedData.clear()
            }
            .map { pagingData ->
                pagingData.map { item ->
                    if (!updatedData.contains(item.id)) {
                        updateModel.emit(item)
                        updatedData.add(item.id)
                    }

                    item
                }
            }
            .cachedIn(coroutineScope),
        accumulatedUpdates,
        accumulatedDeletions
    ) { pagingData, updatedData, deletedData ->
        pagingData
            .filter { item ->
                !deletedData.contains(item.id)
            }
            .map { item ->
                updatedData[item.id] ?: item
            }
    }.cachedIn(coroutineScope)
}