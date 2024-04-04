package com.urlaunched.android.network.sockets

import com.urlaunched.android.common.socket.ActionCableSocketEventMessage
import com.urlaunched.android.network.sockets.models.ActionCableCommandRemoteModel
import com.urlaunched.android.network.sockets.models.ActionCableMessageRemoteModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException
import java.time.Instant

class ActionCableSocket internal constructor(
    private val getSocketUrl: suspend () -> String,
    private val okHttpClient: OkHttpClient,
    coroutineDispatcher: CoroutineDispatcher
) {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        serializersModule = SerializersModule {
            polymorphic(ActionCableMessageRemoteModel::class) {
                defaultDeserializer { ActionCableMessageRemoteModel.Message.serializer() }
            }
        }
    }

    private val coroutineScope = CoroutineScope(coroutineDispatcher + SupervisorJob())

    private var currentWebSocketConnection: WebSocket? = null
    private var isConnected: Boolean = false
    private val activeSubscriptions = mutableListOf<String>()

    private val socketConnectionFlow = callbackFlow<ActionCableSocketEventMessage<JsonElement>> {
        var lastPingTimeMillis: Long? = null

        currentWebSocketConnection?.cancel()
        currentWebSocketConnection = okHttpClient.newWebSocket(
            Request.Builder().url(getSocketUrl()).build(),
            object : WebSocketListener() {
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    close()
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    close(t)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)

                    val message = try {
                        json.decodeFromString<ActionCableMessageRemoteModel>(text)
                    } catch (exception: SerializationException) {
                        launch {
                            send(ActionCableSocketEventMessage.Error(exception))
                        }
                        null
                    }

                    when (message) {
                        is ActionCableMessageRemoteModel.ConfirmSubscription -> {
                            launch {
                                send(ActionCableSocketEventMessage.Subscribed(message.identifier))
                            }
                        }

                        is ActionCableMessageRemoteModel.Disconnect -> {
                            launch {
                                if (message.reconnect == true) {
                                    close(ActionCableStaleConnectionException())
                                } else {
                                    send(ActionCableSocketEventMessage.Disconnected(message.reason))
                                    close()
                                }
                            }
                        }

                        is ActionCableMessageRemoteModel.Message -> {
                            launch {
                                send(
                                    ActionCableSocketEventMessage.Message(
                                        identifier = message.identifier,
                                        body = message.message
                                    )
                                )
                            }
                        }

                        ActionCableMessageRemoteModel.Ping -> {
                            lastPingTimeMillis = Instant.now().toEpochMilli()
                        }

                        is ActionCableMessageRemoteModel.RejectSubscription -> {
                            launch {
                                send(ActionCableSocketEventMessage.SubscriptionRejected(message.identifier))
                            }
                        }

                        ActionCableMessageRemoteModel.Welcome -> {
                            isConnected = true
                            launch {
                                send(ActionCableSocketEventMessage.Connected())
                            }
                        }

                        else -> {}
                    }
                }
            }
        )

        launch {
            while (isActive) {
                lastPingTimeMillis?.also { lastPingTime ->
                    if (Instant.now().toEpochMilli() - lastPingTime > SOCKET_STALE_THRESHOLD) {
                        close(ActionCableStaleConnectionException())
                    }
                }
                delay(LAST_PING_POLL_DELAY)
            }
        }

        awaitClose {
            currentWebSocketConnection?.close(SOCKET_CLOSURE_CODE, null)
            currentWebSocketConnection = null
            isConnected = false
        }
    }.retryWhen { cause, attempt ->
        when {
            cause is ActionCableStaleConnectionException -> {
                true
            }

            cause is IOException && attempt < RETRY_ATTEMPTS -> {
                delay(INITIAL_RETRY_DELAY * attempt)
                true
            }

            else -> {
                false
            }
        }
    }.catch { error ->
        emit(ActionCableSocketEventMessage.Error(error))
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed())

    fun subscribeToChannel(
        channelName: String,
        params: Map<String, JsonElement> = mapOf()
    ): Flow<ActionCableSocketEventMessage<JsonElement>> {
        // We use map here because Action Cable expects identifier element to be a json string with channel name and variable parameters
        val identifier =
            JsonObject((mapOf(IDENTIFIER_CHANNEL_NAME_KEY to JsonPrimitive(channelName)) + params).toSortedMap()).toString()

        return socketConnectionFlow
            .onSubscription {
                val subscriptionExists = activeSubscriptions.contains(identifier)
                activeSubscriptions.add(identifier)
                // If socket is already connected we wouldn't get ActionCableSocketMessage.Connected() event by normal means,
                // so we emit it manually if subscription to chanel with given identity doesn't exist
                if (isConnected && !subscriptionExists) {
                    emit(ActionCableSocketEventMessage.Connected())
                }
            }
            .onEach { message ->
                if (message is ActionCableSocketEventMessage.Connected) {
                    sendSubscribeToChannelCommand(identifier)
                }
            }.filter { message ->
                when (message) {
                    is ActionCableSocketEventMessage.Message -> message.identifier == identifier
                    is ActionCableSocketEventMessage.Subscribed -> message.identifier == identifier
                    is ActionCableSocketEventMessage.SubscriptionRejected -> message.identifier == identifier
                    else -> true
                }
            }.onCompletion {
                // Unsubscribe from channel only if there is no active subscriptions with given identity left to avoid losing subscription
                // in case of several subscriptions with the same identity
                activeSubscriptions.remove(identifier)
                if (!activeSubscriptions.contains(identifier)) {
                    sendUnsubscribeFromChannelCommand(identifier)
                }
            }
    }

    private fun sendUnsubscribeFromChannelCommand(identifier: String) {
        sendCommand(identifier, UNSUBSCRIBE_FROM_CHANNEL_ACTION)
    }

    private fun sendSubscribeToChannelCommand(identifier: String) {
        sendCommand(identifier, SUBSCRIBE_TO_CHANNEL_ACTION)
    }

    private fun sendCommand(identifier: String, command: String) {
        val connectToChannelCommand = json.encodeToString(
            ActionCableCommandRemoteModel(
                identifier = identifier,
                command = command
            )
        )
        currentWebSocketConnection?.send(connectToChannelCommand)
    }

    companion object {
        private const val INITIAL_RETRY_DELAY = 500L
        private const val RETRY_ATTEMPTS = 10
        private const val SOCKET_STALE_THRESHOLD = 6000L // Default action cable client reconnect behaviour
        private const val SOCKET_CLOSURE_CODE = 1000
        private const val SUBSCRIBE_TO_CHANNEL_ACTION = "subscribe"
        private const val UNSUBSCRIBE_FROM_CHANNEL_ACTION = "unsubscribe"
        private const val IDENTIFIER_CHANNEL_NAME_KEY = "channel"
        private const val LAST_PING_POLL_DELAY = 500L
    }
}