package com.urlaunched.synchronizer

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

abstract class BaseSynchronizer {
    val updateModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow()
    val deletedModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow()
    val addModel: MutableSharedFlow<Synchronizable<*>> = MutableSharedFlow()

    inline fun <reified T : Synchronizable<ID>, reified R : Synchronizable<ID>, ID> Flow<PagingData<T>>.synchronize(
        viewModelScope: CoroutineScope,
        relatedType: KClass<R>,
        crossinline map: (T, R) -> T = { t, _ -> t }
    ): Flow<PagingData<T>> {
        val localMap = MutableStateFlow<Map<ID, T>>(mapOf())

        viewModelScope.launch(Dispatchers.IO) {
            updateModel
                .filter { it is T || it is R }
                .collectLatest { item ->
                    item.let { updatedItem ->
                        updatedItem as Synchronizable<ID>

                        localMap.update { currentData ->
                            val updatedMap = currentData.toMutableMap()
                            val currentItem = updatedMap[updatedItem.id]

                            val mappedItem = if (updatedItem is R) {
                                currentItem?.let { current ->
                                    map(current, updatedItem)
                                }
                            } else {
                                updatedItem as T
                            }

                            mappedItem?.let { updatedMap[updatedItem.id] = it }
                            updatedMap
                        }
                    }
                }
        }

        return combine(
            this.cachedIn(viewModelScope),
            localMap
        ) { pagingData, localMapValue ->
            pagingData.map { item ->
                val updatedMap = localMap.value.toMutableMap()
                updatedMap[item.id] = item
                localMap.value = updatedMap
                localMapValue[item.id] ?: item
            }
        }.cachedIn(viewModelScope)
    }

    inline fun <reified T : Synchronizable<ID>, ID> Flow<PagingData<T>>.synchronize(
        viewModelScope: CoroutineScope
    ): Flow<PagingData<T>> {
        val updatedLocalMap = MutableStateFlow<Map<ID, T>>(mapOf())
        val deletedLocalMap = MutableStateFlow<Map<ID, T>>(mapOf())

        viewModelScope.launch(Dispatchers.IO) {
            updateModel.filter { it is T }.collectLatest { item ->
                item.let { updatedItem ->
                    updatedItem as Synchronizable<ID>

                    updatedLocalMap.update { currentData ->
                        val updatedMap = currentData.toMutableMap()

                        updatedMap[updatedItem.id] = updatedItem as T
                        updatedMap
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            deletedModel.filter { it is T }.collectLatest { item ->
                item.let { deletedItem ->
                    deletedItem as Synchronizable<ID>

                    deletedLocalMap.update { currentData ->
                        val deletedMap = currentData.toMutableMap()

                        deletedMap[deletedItem.id] = deletedItem as T
                        deletedMap
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            addModel.filter { it is T }.collectLatest { item ->
                item.let { addedItem ->
                    addedItem as Synchronizable<ID>

                    deletedLocalMap.update { currentData ->
                        val deletedMap = currentData.toMutableMap()
                        deletedMap.remove(addedItem.id)
                        deletedMap
                    }
                }
            }
        }

        return combine(
            this.cachedIn(viewModelScope),
            updatedLocalMap
        ) { pagingData, updatedData ->
            pagingData
                .map { item ->
                    val updatedMap = updatedLocalMap.value.toMutableMap()
                    updatedMap[item.id] = item
                    updatedLocalMap.value = updatedMap
                    updatedData[item.id] ?: item
                }
        }.combine(deletedLocalMap) { pagingData, deletedData ->
            pagingData.filter { item ->
                deletedData.containsKey(item.id).not()
            }
        }.cachedIn(viewModelScope)
    }

    inline fun <reified T : Synchronizable<ID>, reified R : Synchronizable<ID>, ID> List<T>.synchronize(
        viewModelScope: CoroutineScope,
        relatedType: KClass<R>,
        crossinline map: (T, R) -> T = { t, _ -> t }
    ): List<T> {
        val localMap = MutableStateFlow<Map<ID, T>>(mapOf())

        viewModelScope.launch(Dispatchers.IO) {
            updateModel
                .filter { it is T || it is R }
                .collectLatest { item ->
                    item.let { updatedItem ->
                        updatedItem as Synchronizable<ID>

                        localMap.update { currentData ->
                            val updatedMap = currentData.toMutableMap()
                            val currentItem = updatedMap[updatedItem.id]

                            val mappedItem = if (updatedItem is R) {
                                currentItem?.let { current ->
                                    map(current, updatedItem)
                                }
                            } else {
                                updatedItem as T
                            }

                            mappedItem?.let { updatedMap[updatedItem.id] = it }
                            updatedMap
                        }
                    }
                }
        }

        return localMap.value.values.toList()
    }

    inline fun <reified T : Synchronizable<ID>, ID> List<T>.synchronize(viewModelScope: CoroutineScope): List<T> {
        val localMap = MutableStateFlow<Map<ID, T>>(mapOf())

        viewModelScope.launch(Dispatchers.IO) {
            updateModel
                .filter { it is T }
                .collectLatest { item ->
                    item.let { updatedItem ->
                        updatedItem as Synchronizable<ID>

                        localMap.update { currentData ->
                            val updatedMap = currentData.toMutableMap()

                            val mappedItem = updatedItem as T
                            mappedItem.let { updatedMap[updatedItem.id] = it }
                            updatedMap
                        }
                    }
                }
        }

        return localMap.value.values.toList()
    }

    suspend inline fun <reified T : Synchronizable<ID>, reified R : Synchronizable<ID>, ID> T.synchronize(
        crossinline onUpdate: (T) -> Unit,
        relatedType: KClass<R>,
        crossinline map: (R) -> T
    ) {
        updateModel
            .filterNotNull()
            .filter { it is T || it is R }
            .filter { it.id == this.id }
            .collectLatest { updatedItem ->
                when (updatedItem) {
                    is R -> {
                        onUpdate(map(updatedItem))
                    }

                    is T -> {
                        onUpdate(updatedItem)
                    }
                }
            }
    }

    suspend inline fun <reified T : Synchronizable<ID>, ID> T.synchronize(crossinline onUpdate: (T) -> Unit) {
        updateModel
            .filterNotNull()
            .filter { it is T }
            .filter { it.id == this.id }
            .collectLatest { updatedItem ->
                onUpdate(updatedItem as T)
            }
    }

    @Deprecated("Use emitUpdate instead", ReplaceWith("emitUpdate(value)"))
    suspend fun emit(value: Synchronizable<*>) {
        emitUpdate(value)
    }

    suspend fun emitUpdate(value: Synchronizable<*>) {
        updateModel.emit(value)
    }

    suspend fun emitDelete(value: Synchronizable<*>) {
        deletedModel.emit(value)
    }

    suspend fun emitAdd(value: Synchronizable<*>) {
        addModel.emit(value)
    }
}