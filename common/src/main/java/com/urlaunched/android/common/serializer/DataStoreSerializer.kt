package com.urlaunched.android.common.serializer

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

inline fun <reified T> creteDataStoreSerializer(defaultValue: T) = object : Serializer<T> {
    override val defaultValue: T = defaultValue

    override suspend fun readFrom(input: InputStream): T = try {
        Json.decodeFromString(input.readBytes().decodeToString())
    } catch (serialization: SerializationException) {
        defaultValue
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        if (t != null) {
            withContext(Dispatchers.IO) {
                output.write(Json.encodeToString(t).encodeToByteArray())
            }
        }
    }
}