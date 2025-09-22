package com.urlaunched.synchronizer.core

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.urlaunched.android.synchonizer.model.Synchronizable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    val updateModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow()
    val deletedModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow()
    val cancelDeleteModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow()

    inline fun <reified ACTUAL : Synchronizable<ID>, reified RELATED : Synchronizable<ID>, ID> Flow<PagingData<ACTUAL>>.synchronizeRelatedModel(
        viewModelScope: CoroutineScope,
        crossinline mapRelatedToActual: (ACTUAL, RELATED) -> ACTUAL,
        crossinline mapActualToRelated: (ACTUAL) -> RELATED,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<ACTUAL>> {
        val accumulatedUpdates = MutableStateFlow<Map<ID, ACTUAL>>(mapOf())
        val updatedData = mutableSetOf<ID>()

        viewModelScope.launch(coroutineDispatcher) {
            updateModel
                .filterIsInstance<Synchronizable<ID>>()
                .filter { it is RELATED }
                .collectLatest { modelUpdate ->
                    accumulatedUpdates.update { currentData ->
                        val currentItem = currentData[modelUpdate.id]

                        val mappedItem = if (modelUpdate is RELATED) {
                            currentItem?.let { current ->
                                mapRelatedToActual(current, modelUpdate)
                            }
                        } else {
                            modelUpdate as ACTUAL
                        }

                        if (mappedItem != null) {
                            currentData + Pair(mappedItem.id, mappedItem)
                        } else {
                            currentData
                        }
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
                            updateModel.emit(mapActualToRelated(item))
                            updatedData.add(item.id)
                        }

                        item
                    }
                }
                .cachedIn(viewModelScope),
            accumulatedUpdates
        ) { pagingData, updates ->
            pagingData.map { item ->
                updates[item.id] ?: item
            }
        }.cachedIn(viewModelScope)
    }

    inline fun <reified T : Synchronizable<ID>, ID> Flow<PagingData<T>>.synchronize(
        viewModelScope: CoroutineScope,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<T>> {
        val accumulatedUpdates = MutableStateFlow<Map<ID, T>>(mapOf())
        val accumulatedDeletions = MutableStateFlow<Map<ID, T>>(mapOf())
        val updatedData = mutableSetOf<ID>()

        viewModelScope.launch(coroutineDispatcher) {
            updateModel
                .filterIsInstance<T>()
                .collectLatest { modelUpdate ->
                    accumulatedUpdates.update { currentData ->
                        currentData + Pair(modelUpdate.id, modelUpdate)
                    }
                }
        }

        viewModelScope.launch(coroutineDispatcher) {
            deletedModel
                .filterIsInstance<T>()
                .collectLatest { modelDeletion ->
                    accumulatedDeletions.update { currentData ->
                        currentData + Pair(modelDeletion.id, modelDeletion)
                    }
                }
        }

        viewModelScope.launch(coroutineDispatcher) {
            cancelDeleteModel
                .filterIsInstance<T>()
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
                .cachedIn(viewModelScope),
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
        }.cachedIn(viewModelScope)
    }

    suspend inline fun <reified ACTUAL : Synchronizable<ID>, ID> synchronizeList(
        crossinline listGetter: () -> List<ACTUAL>,
        crossinline onListUpdate: (List<ACTUAL>) -> Unit
    ) {
        coroutineScope {
            launch {
                updateModel
                    .filterIsInstance<ACTUAL>()
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
                        .filterIsInstance<ACTUAL>()
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
                        .filterIsInstance<ACTUAL>()
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