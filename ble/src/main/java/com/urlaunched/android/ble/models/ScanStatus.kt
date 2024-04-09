package com.urlaunched.android.ble.models

sealed class ScanStatus {
    data class ScanResults(val advertisements: List<BleAdvertisement>) : ScanStatus()
    data class Failed(val cause: Throwable) : ScanStatus()
    data object BluetoothDisabled : ScanStatus()
}