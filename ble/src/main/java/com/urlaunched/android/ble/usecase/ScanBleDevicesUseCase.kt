package com.urlaunched.android.ble.usecase

import com.benasher44.uuid.uuidFrom
import com.juul.kable.AndroidAdvertisement
import com.juul.kable.Bluetooth
import com.juul.kable.BluetoothDisabledException
import com.juul.kable.Filter
import com.urlaunched.android.ble.models.BleAdvertisement
import com.urlaunched.android.ble.models.ScanStatus
import com.urlaunched.android.ble.scanner.BleScanner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlin.coroutines.cancellation.CancellationException

class ScanBleDevicesUseCase(private val bleScanner: BleScanner) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(uuidFilter: String) = Bluetooth.availability.flatMapLatest { bluetoothAvailability ->
        if (bluetoothAvailability is Bluetooth.Availability.Available) {
            getScannerFlow(uuidFilter)
        } else {
            flowOf(ScanStatus.BluetoothDisabled)
        }
    }

    private fun getScannerFlow(uuidFilter: String) =
        bleScanner.scanDevices(scanFilters = listOf(Filter.Service(uuidFrom(uuidFilter))))
            .scan(mapOf<String, AndroidAdvertisement>()) { accumulator, value ->
                accumulator + (value.address to value) // Accumulate and filter out duplicated devices over several scans by their address
            }
            .map<Map<String, AndroidAdvertisement>, ScanStatus> { foundDevices ->
                ScanStatus.ScanResults(
                    foundDevices.values
                        .filter { it.isConnectable == true }
                        .map { device ->
                            BleAdvertisement(
                                name = device.name.orEmpty(),
                                address = device.address
                            )
                        }
                )
            }
            .catch { cause ->
                when (cause) {
                    is CancellationException -> {
                        throw cause // Cancellation exception need to be rethrown for coroutines cancellations guarantees
                    }

                    is BluetoothDisabledException -> {
                        emit(ScanStatus.BluetoothDisabled)
                    }

                    is SecurityException -> {
                        emit(ScanStatus.BluetoothDisabled)
                    }

                    else -> {
                        emit(ScanStatus.Failed(cause))
                    }
                }
            }
}