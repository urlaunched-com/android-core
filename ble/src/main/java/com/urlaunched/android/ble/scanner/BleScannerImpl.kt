package com.urlaunched.android.ble.scanner

import com.juul.kable.AndroidAdvertisement
import com.juul.kable.Filter
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import kotlinx.coroutines.flow.Flow

class BleScannerImpl(private val loggingEnabled: Boolean) : BleScanner {
    override fun scanDevices(scanFilters: List<Filter>?): Flow<AndroidAdvertisement> = Scanner {
        logging {
            level = if (loggingEnabled) Logging.Level.Data else Logging.Level.Warnings
        }
        filters = scanFilters
    }.advertisements
}