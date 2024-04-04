package com.urlaunched.android.network.sockets.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ActionCableCommandRemoteModel(
    @SerialName("identifier")
    val identifier: String,
    @SerialName("command")
    val command: String
)