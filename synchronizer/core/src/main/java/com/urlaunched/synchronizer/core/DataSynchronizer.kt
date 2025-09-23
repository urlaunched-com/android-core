package com.urlaunched.synchronizer.core

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.urlaunched.android.synchonizer.model.Synchronizable
import com.urlaunched.synchronizer.core.DataSynchronizer.cancelDeleteModel
import com.urlaunched.synchronizer.core.DataSynchronizer.deletedModel
import com.urlaunched.synchronizer.core.DataSynchronizer.updateModel
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

object DataSynchronizer {
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

    suspend inline fun <reified MODEL : Synchronizable<ID>, ID> synchronizeList(
        crossinline listGetter: () -> List<MODEL>,
        crossinline onListUpdate: (List<MODEL>) -> Unit
    ) {
        coroutineScope {
            launch {
                updateModel
                    .filterIsInstance<MODEL>()
                    .collectLatest { updatedData ->
                        val currentList = listGetter()

                        onListUpdate(
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
                        .filterIsInstance<MODEL>()
                        .collectLatest { deletedModel ->
                            val currentList = listGetter()

                            currentList.indexOf(deletedModel).takeIf { it != -1 }?.let { index ->
                                deletedItemsPositions.put(deletedModel.id, index)

                                onListUpdate(
                                    currentList.filter { item -> item.id != deletedModel.id }
                                )
                            }
                        }
                }

                launch {
                    cancelDeleteModel
                        .filterIsInstance<MODEL>()
                        .collectLatest { cancelDeleteModel ->
                            val currentList = listGetter()

                            deletedItemsPositions[cancelDeleteModel.id]?.let { index ->
                                deletedItemsPositions.remove(cancelDeleteModel.id)
                                onListUpdate(
                                    currentList.toMutableList().apply { add(index, cancelDeleteModel) }
                                )
                            }
                        }
                }
            }
        }
    }

    suspend inline fun <reified ACTUAL : Synchronizable<ID>, reified RELATED : Synchronizable<ID>, ID> synchronizeRelatedModelList(
        crossinline listGetter: () -> List<ACTUAL>,
        crossinline mapRelatedToActual: (ACTUAL, RELATED) -> ACTUAL,
        crossinline onListUpdate: (List<ACTUAL>) -> Unit
    ) {
        updateModel
            .filterIsInstance<RELATED>()
            .collectLatest { updatedData ->
                val currentList = listGetter()

                onListUpdate(
                    currentList.map { currentItem ->
                        if (currentItem.id == updatedData.id) {
                            mapRelatedToActual(currentItem, updatedData)
                        } else {
                            currentItem
                        }
                    }
                )
            }
    }

    suspend inline fun <reified MODEL : Synchronizable<ID>, ID> synchronizeModel(
        id: ID,
        crossinline onUpdate: (MODEL) -> Unit
    ) {
        updateModel
            .filterIsInstance<MODEL>()
            .filter { it.id == id }
            .collectLatest { updatedItem ->
                onUpdate(updatedItem)
            }
    }

    inline fun <reified MODEL : Synchronizable<*>> getUpdatesFlow() = updateModel.filterIsInstance<MODEL>()

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

inline fun <reified ACTUAL : Synchronizable<ID>, reified RELATED : Synchronizable<ID>, ID> Flow<PagingData<ACTUAL>>.synchronizeRelatedModel(
    coroutineScope: CoroutineScope,
    crossinline mapRelatedToActual: (ACTUAL, RELATED) -> ACTUAL,
    crossinline mapActualToRelated: (ACTUAL) -> RELATED?,
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