package com.urlaunched.android.ble.scanner

import com.juul.kable.AndroidAdvertisement
import com.juul.kable.Filter
import kotlinx.coroutines.flow.Flow

interface BleScanner {
    fun scanDevices(scanFilters: List<Filter>? = null): Flow<AndroidAdvertisement>
}