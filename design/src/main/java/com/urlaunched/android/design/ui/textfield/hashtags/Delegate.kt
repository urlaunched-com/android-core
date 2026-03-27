package com.urlaunched.android.design.ui.textfield.hashtags

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable

abstract class Delegate(coroutineDispatcher: CoroutineDispatcher) : Closeable {
    private val delegateScope = CoroutineScope(coroutineDispatcher + SupervisorJob())

    override fun close() {
        delegateScope.cancel()
    }
}