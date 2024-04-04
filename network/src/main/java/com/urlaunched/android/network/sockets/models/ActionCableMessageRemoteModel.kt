package com.urlaunched.android.network.sockets.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonElement

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
internal sealed class ActionCableMessageRemoteModel {
    @Serializable
    @SerialName("ping")
    internal data object Ping : ActionCableMessageRemoteModel()

    @Serializable
    @SerialName("welcome")
    internal data object Welcome : ActionCableMessageRemoteModel()

    @Serializable
    @SerialName("confirm_subscription")
    internal data class ConfirmSubscription(
        @SerialName("identifier")
        val identifier: String
    ) : ActionCableMessageRemoteModel()

    @Serializable
    @SerialName("reject_subscription")
    internal data class RejectSubscription(
        @SerialName("identifier")
        val identifier: String
    ) : ActionCableMessageRemoteModel()

    @Serializable
    @SerialName("disconnect")
    internal data class Disconnect(
        @SerialName("reason")
        val reason: String?,
        @SerialName("reconnect")
        val reconnect: Boolean?
    ) : ActionCableMessageRemoteModel()

    @Serializable
    internal data class Message(
        @SerialName("identifier")
        val identifier: String,
        @SerialName("message")
        val message: JsonElement?
    ) : ActionCableMessageRemoteModel()
}