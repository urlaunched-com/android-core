package com.urlaunched.android.common.socket.socketmanager

interface SocketEvent<T> {
    val data: T

    interface Updated<T> : SocketEvent<T> {
        override val data: T
    }
    interface New<T> : SocketEvent<T> {
        override val data: T
    }
    interface Delete<T> : SocketEvent<T> {
        override val data: T
    }
}