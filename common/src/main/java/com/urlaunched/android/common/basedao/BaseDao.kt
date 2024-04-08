package com.urlaunched.android.common.basedao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    suspend fun insertOrUpdate(obj: T) {
        if (insert(obj) == -1L) update(obj)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(objects: List<T>): List<Long>

    @Delete
    suspend fun delete(obj: T)

    @Delete
    suspend fun delete(obj: List<T>)

    @Update
    suspend fun update(obj: T)

    @Update
    suspend fun update(objects: List<T>)
}