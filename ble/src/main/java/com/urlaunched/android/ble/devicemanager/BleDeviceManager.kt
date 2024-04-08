package com.urlaunched.android.ble.devicemanager

import com.urlaunched.android.ble.models.DeviceState
import com.urlaunched.android.common.response.Response
import kotlinx.coroutines.flow.Flow

interface BleDeviceManager {
    fun connectToDevice(address: String): Flow<DeviceState>
    suspend fun reconnect()
    fun observeCharacteristic(
        service: String,
        characteristic: String,
        onSubscription: suspend () -> Unit = {}
    ): Flow<ByteArray>
    suspend fun readCharacteristic(service: String, characteristic: String): Response<ByteArray>
    suspend fun writeData(service: String, characteristic: String, data: ByteArray): Response<Unit>
}