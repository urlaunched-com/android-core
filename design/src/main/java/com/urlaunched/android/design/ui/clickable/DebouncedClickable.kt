package com.urlaunched.android.design.ui.clickable

import android.os.SystemClock
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

const val DEFAULT_ACTION_DEBOUNCE_TIME = 500L

@Composable
inline fun debouncedAction(
    crossinline block: () -> Unit,
    debounceTime: Long = DEFAULT_ACTION_DEBOUNCE_TIME
): () -> Unit {
    var lastExecutionTime by remember { mutableLongStateOf(0L) }
    return {
        val now = SystemClock.uptimeMillis()
        if (now - lastExecutionTime > debounceTime) {
            block()
        }
        lastExecutionTime = now
    }
}

@Composable
fun Modifier.debouncedClickable(
    debounceTime: Long = DEFAULT_ACTION_DEBOUNCE_TIME,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = LocalIndication.current,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = this.clickable(
    interactionSource = interactionSource,
    indication = indication,
    enabled = enabled,
    onClickLabel = onClickLabel,
    role = role,
    onClick = debouncedAction(debounceTime = debounceTime, block = { onClick() })
)