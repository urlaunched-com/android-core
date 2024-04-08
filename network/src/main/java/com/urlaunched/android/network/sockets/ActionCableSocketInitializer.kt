package com.urlaunched.android.network.sockets

import com.urlaunched.android.network.okhttp.OkHttpInitializer
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient

object ActionCableSocketInitializer {
    fun createGeneralActionCableSocket(
        enableLogging: Boolean,
        coroutineDispatcher: CoroutineDispatcher,
        getSocketUrl: suspend () -> String,
        okHttpClient: OkHttpClient? = null
    ): ActionCableSocket = ActionCableSocket(
        getSocketUrl = getSocketUrl,
        okHttpClient = OkHttpInitializer.createGeneralOkHttpClient(
            enableLogging = enableLogging,
            okHttpClient = okHttpClient
        ),
        coroutineDispatcher = coroutineDispatcher
    )
}