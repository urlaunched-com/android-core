package com.urlaunched.android.design.ui.downloadingprogressdialog

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

private const val MIN_ANIM_DURATION = 1
private const val MAX_ANIM_DURATION = 700

@Composable
internal fun animateProgressAsState(progress: Float): State<Float> {
    val coroutineScope = rememberCoroutineScope()
    val animatedProgress = remember { Animatable(progress) }
    var lastTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(progress) {
        val now = System.currentTimeMillis()
        val deltaT = (now - lastTimestamp).coerceAtLeast(MIN_ANIM_DURATION.toLong())

        coroutineScope.launch {
            animatedProgress.animateTo(
                targetValue = progress,
                animationSpec = tween(
                    durationMillis = deltaT.toInt().coerceIn(
                        minimumValue = MIN_ANIM_DURATION,
                        maximumValue = MAX_ANIM_DURATION
                    )
                )
            )
        }

        lastTimestamp = now
    }
    return animatedProgress.asState()
}