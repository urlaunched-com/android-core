package com.urlaunched.android.ble.devicemanager

import android.content.Context
import android.content.pm.PackageManager
import com.juul.kable.Bluetooth
import com.juul.kable.Peripheral
import com.juul.kable.State
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import com.juul.kable.peripheral
import com.urlaunched.android.ble.models.DeviceState
import com.urlaunched.android.ble.models.toDeviceState
import com.urlaunched.android.ble.utils.BlePermissionsUtil
import com.urlaunched.android.ble.utils.toResponse
import com.urlaunched.android.common.response.Response
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

class BleDeviceManagerImpl(
    private val loggingEnabled: Boolean,
    coroutineDispatcher: CoroutineDispatcher,
    context: Context
) : BleDeviceManager {
    private val coroutineScope = CoroutineScope(coroutineDispatcher + SupervisorJob())
    private var childCoroutineScope = coroutineScope.childScope()

    private val currentPeripheral = MutableStateFlow<Peripheral?>(null)
    private val reconnectFailed = MutableStateFlow(false)
    private val currentAddress = MutableStateFlow<String?>(null)

    private var connectionAttempt = 0

    private val reconnectChannel = Channel<Unit>()

    private val permissionsGranted = flow {
        while (true) {
            emit(
                BlePermissionsUtil.getBlePermissionsForApi()
                    .all { permission -> context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED }
            )
            delay(1.seconds)
        }
    }
        .distinctUntilChanged()
        .shareIn(scope = coroutineScope, started = SharingStarted.WhileSubscribed(), replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val bleConnection = combine(
        currentAddress.filterNotNull(),
        // Use channel here for ability to rebuild connection flow on event to be able to reconnect to a device by user action
        reconnectChannel
            .receiveAsFlow()
            .onStart {
                emit(Unit)
            }
    ) { address, _ -> address }.flatMapLatest { address ->
        // Cancel current connection scopes, if any and create new one to avoid duplicate connections
        childCoroutineScope.cancel()
        childCoroutineScope = coroutineScope.childScope()
        reconnectFailed.value = false
        connectionAttempt = 0

        childCoroutineScope.peripheral(address) {
            logging {
                level = if (loggingEnabled) Logging.Level.Data else Logging.Level.Events
            }
        }.let { peripheral ->
            currentPeripheral.value = peripheral
            enableAutoReconnect(childCoroutineScope, peripheral)

            combine(
                peripheral.state,
                Bluetooth.availability,
                permissionsGranted
            ) { state, bluetooth, permissionsGranted ->
                if (bluetooth is Bluetooth.Availability.Unavailable || !permissionsGranted) {
                    DeviceState.BluetoothUnavailable
                } else {
                    state.toDeviceState(connectionAttempt)
                }
            }
                .combine(reconnectFailed) { state, reconnectFailed ->
                    // If current state is Disconnected but we still trying to reconnect,
                    // overwrite status to Connecting to avoid rapid status changes from Disconnected to Connected and vice versa
                    if (state is DeviceState.Disconnected && !reconnectFailed) {
                        DeviceState.Connecting(connectionAttempt)
                    } else {
                        state
                    }
                }
                .onCompletion {
                    currentPeripheral.value = null
                    childCoroutineScope.cancel()
                }
        }
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(connectionKeepAliveTime), replay = 1)

    override fun connectToDevice(address: String): Flow<DeviceState> {
        currentAddress.value = address
        return bleConnection
    }

    override suspend fun reconnect() {
        childCoroutineScope.cancel()
        currentPeripheral.value = null
        reconnectChannel.send(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCharacteristic(
        service: String,
        characteristic: String,
        onSubscription: suspend () -> Unit
    ): Flow<ByteArray> = // Resubscribe on every reconnection
        bleConnection.filterIsInstance<DeviceState.Connected>().flatMapLatest {
            currentPeripheral.filterNotNull().flatMapLatest { peripheral ->
                peripheral
                    .observe(
                        characteristicOf(
                            service = service,
                            characteristic = characteristic
                        )
                    )
                    .onStart {
                        onSubscription()
                    }
            }
        }

    override suspend fun readCharacteristic(service: String, characteristic: String): Response<ByteArray> =
        runCatching {
            currentPeripheral.filterNotNull().first().suspendUntil<State.Connected>().read(
                characteristicOf(service = service, characteristic = characteristic)
            )
        }.toResponse()

    override suspend fun writeData(service: String, characteristic: String, data: ByteArray): Response<Unit> =
        runCatching {
            currentPeripheral.filterNotNull().first().suspendUntil<State.Connected>().write(
                characteristic = characteristicOf(service = service, characteristic = characteristic),
                data = data
            )
        }.toResponse()

    // Connects or reconnects to BLE device if no connection is established and bluetooth is on
    // Will retry connection for several times with a delay until giving up
    private fun enableAutoReconnect(scope: CoroutineScope, peripheral: Peripheral) {
        scope.launch {
            combine(Bluetooth.availability, peripheral.state, permissionsGranted, BleDeviceManagerImpl::AutoConnectInfo)
                .filter { (bluetoothAvailability, connectionState, permissionsGranted) ->
                    bluetoothAvailability == Bluetooth.Availability.Available && connectionState is State.Disconnected && permissionsGranted
                }.collect {
                    // Ble connection status transitions from Disconnected to Connecting and again to Disconnected on connect failure,
                    // so this collectLatest block will be triggered again
                    // when bluetooth is ready for another connection attempt without need for loops
                    scope.ensureActive()
                    connectionAttempt++

                    delay(reconnectDelay)

                    try {
                        peripheral.connect()
                        connectionAttempt = 0
                    } catch (error: Exception) {
                        if (error is CancellationException) {
                            throw error // Rethrow cancellation exception to honour coroutines cancellation guaranties
                        }
                    }
                }
        }
    }

    private fun CoroutineScope.childScope() = CoroutineScope(coroutineContext + Job(coroutineContext[Job]))

    private suspend inline fun <reified T : State> Peripheral.suspendUntil(): Peripheral {
        state.first { it is T }
        return this
    }

    private data class AutoConnectInfo(
        val bluetoothAvailability: Bluetooth.Availability,
        val peripheralState: State,
        val permissionsGranted: Boolean
    )

    companion object {
        private val reconnectDelay = 1.seconds
        private val connectionKeepAliveTime = 5.seconds
    }
}