package com.urlaunched.android.ble.utils

import android.Manifest
import android.os.Build

object BlePermissionsUtil {
    fun getBlePermissionsForApi() = when {
        // If your app targets Android 9 (API level 28) or lower, you can declare the ACCESS_COARSE_LOCATION permission
        // instead of the ACCESS_FINE_LOCATION permission.
        // https://developer.android.com/guide/topics/connectivity/bluetooth/permissions#declare-android11-or-lower
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> listOf(Manifest.permission.ACCESS_COARSE_LOCATION)

        // ACCESS_FINE_LOCATION is necessary because, on Android 11 (API level 30) and lower, a Bluetooth scan could
        // potentially be used to gather information about the location of the user.
        // https://developer.android.com/guide/topics/connectivity/bluetooth/permissions#declare-android11-or-lower
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.R -> listOf(Manifest.permission.ACCESS_FINE_LOCATION)

        // If your app targets Android 12 (API level 31) or higher, declare the following permissions in your app's
        // manifest file:
        //
        // 1. If your app looks for Bluetooth devices, such as BLE peripherals, declare the `BLUETOOTH_SCAN` permission.
        // 2. If your app makes the current device discoverable to other Bluetooth devices, declare the
        //    `BLUETOOTH_ADVERTISE` permission.
        // 3. If your app communicates with already-paired Bluetooth devices, declare the BLUETOOTH_CONNECT permission.
        // https://developer.android.com/guide/topics/connectivity/bluetooth/permissions#declare-android12-or-higher
        else -> listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    }
}