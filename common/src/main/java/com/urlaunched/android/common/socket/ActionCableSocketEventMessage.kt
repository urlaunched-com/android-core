package com.urlaunched.android.common.socket

sealed class ActionCableSocketEventMessage<T> {
    class Connected<T> : ActionCableSocketEventMessage<T>()
    data class Disconnected<T>(val reason: String?) : ActionCableSocketEventMessage<T>()
    data class Subscribed<T>(val identifier: String) : ActionCableSocketEventMessage<T>()
    data class SubscriptionRejected<T>(val identifier: String) : ActionCableSocketEventMessage<T>()
    data class Message<T>(val identifier: String, val body: T?) : ActionCableSocketEventMessage<T>()
    data class Error<T>(val error: Throwable?) : ActionCableSocketEventMessage<T>()
}

fun <T : Any, R : Any> ActionCableSocketEventMessage<T>.map(convert: (T?) -> R?): ActionCableSocketEventMessage<R> =
    try {
        when (this) {
            is ActionCableSocketEventMessage.Connected -> ActionCableSocketEventMessage.Connected()

            is ActionCableSocketEventMessage.Disconnected -> ActionCableSocketEventMessage.Disconnected(reason = reason)

            is ActionCableSocketEventMessage.Error -> ActionCableSocketEventMessage.Error(error = error)

            is ActionCableSocketEventMessage.Message -> ActionCableSocketEventMessage.Message(
                identifier = identifier,
                body = convert(body)
            )

            is ActionCableSocketEventMessage.Subscribed -> ActionCableSocketEventMessage.Subscribed(identifier = identifier)

            is ActionCableSocketEventMessage.SubscriptionRejected -> ActionCableSocketEventMessage.SubscriptionRejected(
                identifier = identifier
            )
        }
    } catch (e: Exception) {
        ActionCableSocketEventMessage.Error(error = e)
    }