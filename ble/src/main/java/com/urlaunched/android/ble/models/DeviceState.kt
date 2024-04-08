package com.urlaunched.android.ble.models

import com.juul.kable.State

sealed class DeviceState {
    data class Disconnected(val cause: String?) : DeviceState()
    data class Connecting(val attempt: Int = 0) : DeviceState()
    data object Connected : DeviceState()
    data object BluetoothUnavailable : DeviceState()
}

internal fun State.toDeviceState(connectionAttempt: Int = 0) = when (this) {
    is State.Connected -> DeviceState.Connected
    is State.Connecting.Bluetooth -> DeviceState.Connecting(connectionAttempt)
    is State.Connecting.Observes -> DeviceState.Connecting(connectionAttempt)
    is State.Connecting.Services -> DeviceState.Connecting(connectionAttempt)
    is State.Disconnecting -> DeviceState.Connected
    is State.Disconnected -> DeviceState.Disconnected(this.status.toString())
}